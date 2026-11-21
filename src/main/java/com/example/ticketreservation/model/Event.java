package com.example.ticketreservation.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Document(collection = "events")
public class Event {

    @Id 
    private String id;
    
    private String name;
    private String venue;
    private LocalDateTime eventDate;
    private String description;
    private int availableSeats;
    private int totalSeats;
    private double ticketPrice;
    private String organizer;
    
}
