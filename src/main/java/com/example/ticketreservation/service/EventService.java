package com.example.ticketreservation.service;

import java.util.List;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.ticketreservation.dto.EventRequest;
import com.example.ticketreservation.dto.EventResponse;
import com.example.ticketreservation.exception.ApiException;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.ReservationStatus;
import com.example.ticketreservation.repository.EventRepository;
import com.example.ticketreservation.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class EventService {

    private final MongoTemplate mongoTemplate;
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;

    public List<EventResponse> findAll(String name){
        List<Event> events = (name == null || name.isBlank()) 
                ? eventRepository.findAll()
                : eventRepository.findByNameContainingIgnoreCase(name);

        return events.stream().map(EventResponse::from).toList();
    }

    public EventResponse findById(String id){
        return eventRepository.findById(id)
                .map(EventResponse::from)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found: " + id));
    }

    public EventResponse create(EventRequest req, Authentication auth){

        Event event = eventRepository.save(Event.builder().
                name(req.name())
                .venue(req.venue())
                .eventDate(req.eventDate())
                .description(req.description())
                .totalSeats(req.totalSeats())
                .availableSeats(req.totalSeats())
                .ticketPrice(req.ticketPrice())
                .organizer(auth.getName())
                .build()
            );

        return EventResponse.from(event);
    }

        public EventResponse update(String id, EventRequest req, Authentication auth) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found: " + id));

        if (!isAdmin(auth) && !existing.getOrganizer().equals(auth.getName())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only edit your own events");
        }

        int delta = req.totalSeats() - existing.getTotalSeats();

        Query query = Query.query(Criteria.where("id").is(id));
        if (delta < 0) {
            query.addCriteria(Criteria.where("availableSeats").gte(-delta));
        }

        Update update = new Update()
                .set("name", req.name())
                .set("description", req.description())
                .set("venue", req.venue())
                .set("eventDate", req.eventDate())
                .set("totalSeats", req.totalSeats())
                .set("ticketPrice", req.ticketPrice())
                .inc("availableSeats", delta);

        Event updated = mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Event.class);

        if (updated == null) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Cannot reduce total seats below the number already booked");
        }
        return EventResponse.from(updated);
    }

    public void delete(String id) {
        if (!eventRepository.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Event not found: " + id);
        }
        
        if (reservationRepository.existsByEventIdAndStatus(id, ReservationStatus.CONFIRMED)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Event has confirmed reservations and cannot be deleted");
        }
        eventRepository.deleteById(id);
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
    
    public Event takeSeats(String eventId, int seats) {
        Event updated = mongoTemplate.findAndModify(
            Query.query(Criteria.where("id").is(eventId).and("availableSeats").gte(seats)),
            new Update().inc("availableSeats", -seats),
            FindAndModifyOptions.options().returnNew(true),
            Event.class
        );

        if(updated == null) {
            throw eventRepository.existsById(eventId)
                    ? new ApiException(HttpStatus.CONFLICT, "Not enough seats available")
                    : new ApiException(HttpStatus.NOT_FOUND, "Event not found: " + eventId);
        }
        return updated;
    }

    public void releaseSeats(String eventId, int seats){
        mongoTemplate.findAndModify(
            Query.query(Criteria.where("id").is(eventId)),
            new Update().inc("availableSeats", seats),
            Event.class
        );
    }
}
