package com.example.ticketreservation.dto;
import java.time.LocalDateTime;
import com.example.ticketreservation.model.Event;

public record EventResponse(
    String id, String name, String description, String venue, LocalDateTime eventDate,
    int totalSeats, int availableSeats, double ticketPrice, String organizer
) {
    public static EventResponse from(Event e) {
        return new EventResponse(e.getId(), e.getName(), e.getDescription(), e.getVenue(),
                                e.getEventDate(), e.getTotalSeats(), e.getAvailableSeats(),
                                e.getTicketPrice(), e.getOrganizer());
    }
}
