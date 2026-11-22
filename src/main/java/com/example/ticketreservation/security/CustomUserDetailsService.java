package com.example.ticketreservation.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.ticketreservation.model.AppUser;
import com.example.ticketreservation.repository.AppUserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Loads the user, BCrypt hash and role from MongoDB.
 * The role becomes the authority "ROLE_ADMIN" / "ROLE_ORGANIZER" / "ROLE_CUSTOMER",
 * which is what hasRole('ADMIN') etc. in @PreAuthorize look for.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
}
