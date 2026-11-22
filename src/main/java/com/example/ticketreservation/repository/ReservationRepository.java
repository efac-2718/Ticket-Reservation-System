package com.example.ticketreservation.repository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.ticketreservation.model.Reservation;
import com.example.ticketreservation.model.ReservationStatus;

public interface ReservationRepository extends MongoRepository<Reservation, String> {

    List<Reservation> findByUsername(String username);
    List<Reservation> findByEventIdIn(Collection<String> eventIds);
    boolean existsByEventIdAndStatus(String eventId, ReservationStatus status);
    Optional<Reservation> findByReservationCode(String reservationCode);
    List<Reservation> findByStatus(ReservationStatus status);
}
