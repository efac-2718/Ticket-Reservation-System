package com.example.ticketreservation.controller;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticketreservation.dto.StatsResponse;
import com.example.ticketreservation.dto.UserResponse;
import com.example.ticketreservation.service.AdminService;

import lombok.RequiredArgsConstructor;


@RestController 
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor 
public class AdminController {

    private final AdminService adminService;
    
    @GetMapping("/users")
    public List<UserResponse> users() {
        return adminService.listUsers();
    }
    
    @GetMapping("/stats")
    public StatsResponse stats() {
        return adminService.getStats();
    }
    
}
