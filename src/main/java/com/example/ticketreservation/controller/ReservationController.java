package com.example.ticketreservation.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;



@RestController 
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    
    @PostMapping("/api/reservations")
    public String postMethodName() {
        
    }

    @GetMapping("/api/reservations/my")
    public String getCustomerReservation(@RequestParam String param) {
        return new String();
    }

    @GetMapping("/api/reservations")
    public String getReservations(@RequestParam String param) {
        return new String();
    }
    
    @DeleteMapping("/api/reservations/{id}")
    public String deleteReservation(){
        return new String();
    }
    
}
