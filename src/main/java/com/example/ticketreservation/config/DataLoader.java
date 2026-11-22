package com.example.ticketreservation.config;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.ticketreservation.model.AppUser;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Role;
import com.example.ticketreservation.repository.AppUserRepository;
import com.example.ticketreservation.repository.EventRepository;

import lombok.RequiredArgsConstructor;

/**
 * Runs once when the application starts.
 * Creates the seeded accounts from the lab sheet (Task 2) and a few sample events,
 * but only if they don't exist yet, so restarting the app never creates duplicates.
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final AppUserRepository appUserRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createUserIfMissing("admin", "admin123", "System Administrator", Role.ADMIN);
        createUserIfMissing("organizer", "organizer123", "Event Organizer", Role.ORGANIZER);
        createUserIfMissing("customer", "customer123", "Sample Customer", Role.CUSTOMER);

        if (eventRepository.count() == 0) {
            eventRepository.saveAll(List.of(
                    sampleEvent("New Year Music Festival", "Live bands and fireworks", "Colombo",
                            LocalDateTime.of(2030, 12, 31, 20, 0), 500, 2500),
                    sampleEvent("Tech Talk: Spring Boot and MongoDB", "Building secure REST APIs", "Kandy",
                            LocalDateTime.of(2030, 6, 15, 17, 30), 100, 0),
                    sampleEvent("Drama Night", "An evening of local theatre", "Galle",
                            LocalDateTime.of(2030, 9, 20, 19, 0), 200, 1500)));
            log.info("Seeded 3 sample events");
        }
    }

    private void createUserIfMissing(String username, String rawPassword, String fullName, Role role) {
        if (appUserRepository.existsByUsername(username)) {
            return;
        }
        appUserRepository.save(new AppUser(
                null,
                username,
                passwordEncoder.encode(rawPassword),    // stored as a BCrypt hash ($2a$10$...)
                fullName,
                role));
        log.info("Seeded user '{}' with role {}", username, role);
    }

    private Event sampleEvent(String name, String description, String venue,
                              LocalDateTime date, int seats, double price) {
        return Event.builder()
                .name(name)
                .description(description)
                .venue(venue)
                .eventDate(date)
                .totalSeats(seats)
                .availableSeats(seats)
                .ticketPrice(price)
                .organizer("organizer")                 // owned by the seeded organizer account
                .build();
    }
}
