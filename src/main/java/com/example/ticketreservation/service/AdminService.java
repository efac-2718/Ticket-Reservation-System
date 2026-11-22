package com.example.ticketreservation.service;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.ticketreservation.dto.StatsResponse;
import com.example.ticketreservation.dto.UserResponse;
import com.example.ticketreservation.model.Reservation;
import com.example.ticketreservation.model.ReservationStatus;
import com.example.ticketreservation.repository.AppUserRepository;
import com.example.ticketreservation.repository.EventRepository;
import com.example.ticketreservation.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AdminService {

    private final AppUserRepository appUserRepository;
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;

    public List<UserResponse> listUsers(){
        return appUserRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    public StatsResponse getStats(){
        List<Reservation> confirmed = reservationRepository.findByStatus(ReservationStatus.CONFIRMED);

        long ticketsSold = confirmed.stream().mapToLong(Reservation::getSeats).sum();
        double revenue = confirmed.stream().mapToDouble(Reservation::getAmount).sum();

        return new StatsResponse(appUserRepository.count(), eventRepository.count(), ticketsSold, revenue);
    }
    
}
 