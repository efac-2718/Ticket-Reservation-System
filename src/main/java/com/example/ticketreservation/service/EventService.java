package com.example.ticketreservation.service;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.ticketreservation.exception.ApiException;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class EventService {

    private final MongoTemplate mongoTemplate;
    private final EventRepository eventRepository;
    
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
                    : new ApiException(HttpStatus.NOT_FOUND, "Event not found" + eventId);
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
