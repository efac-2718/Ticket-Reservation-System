package com.example.ticketreservation.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.ticketreservation.dto.ReservationRequest;
import com.example.ticketreservation.dto.ReservationResponse;
import com.example.ticketreservation.exception.ApiException;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Reservation;
import com.example.ticketreservation.model.ReservationStatus;
import com.example.ticketreservation.repository.EventRepository;
import com.example.ticketreservation.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class ReservationService {
    
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final EventService eventService;

    public ReservationResponse book (ReservationRequest req, String username){

        Event event = eventService.takeSeats(req.eventId(), req.seats());

        Reservation saved = reservationRepository.save(Reservation.builder()
                .reservationCode("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .username(username)
                .eventId(event.getId())
                .seats(req.seats())
                .amount(req.seats() * event.getTicketPrice())
                .status(ReservationStatus.CONFIRMED)
                .build()
            );

            return ReservationResponse.from(saved);
    }

    public List<ReservationResponse> findMine(Authentication auth){
        return reservationRepository.findByUsername(auth.getName()).stream().map(ReservationResponse::from).toList();
    }

    public List<ReservationResponse> findAll(Authentication auth){
        List<Reservation> reservations;
        if(isAdmin(auth)) {
            reservations = reservationRepository.findAll();
        } else {
            List<String> myEventIds = eventRepository.findByOrganizer(auth.getName()).stream().map(Event::getId).toList();
            reservations = reservationRepository.findByEventIdIn(myEventIds);
        }
        return reservationRepository.findAll().stream().map(ReservationResponse::from).toList();
    }

    public ReservationResponse cancel(String id, Authentication auth){

        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Reservation not found: "+id));

        if (!isAdmin(auth) && !r.getUsername().equals(auth.getName())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only cancel your own reservations");
        }

        if (r.getStatus() == ReservationStatus.CANCELLED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Reservation is already cancelled");
        }

        eventService.releaseSeats(r.getEventId(), r.getSeats());
        r.setStatus(ReservationStatus.CANCELLED);
        return ReservationResponse.from(reservationRepository.save(r));
    }

    private boolean isAdmin(Authentication auth){
        return auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
