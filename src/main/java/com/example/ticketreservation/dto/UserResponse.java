package com.example.ticketreservation.dto;

import com.example.ticketreservation.model.AppUser;
import com.example.ticketreservation.model.Role;

public record UserResponse(
    String id, String username, String fullName, Role role
) {
    public static UserResponse from (AppUser u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getFullName(), u.getRole());
    }

}
