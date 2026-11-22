package com.example.ticketreservation.controller;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticketreservation.dto.ReservationRequest;
import com.example.ticketreservation.dto.ReservationResponse;
import com.example.ticketreservation.service.ReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@RestController 
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    
    @PostMapping
    @PreAuthorize ("hasRole('CUSTOMER')")
    public ResponseEntity<ReservationResponse> book(@Valid @RequestBody ReservationRequest req, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.book(req,auth.getName()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<ReservationResponse> mine(Authentication auth){
        return reservationService.findMine(auth);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    public List<ReservationResponse> all(Authentication auth) {
        return reservationService.findAll(auth);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ReservationResponse cancel(@PathVariable String id, Authentication auth){
        return reservationService.cancel(id, auth);
    } 
}
