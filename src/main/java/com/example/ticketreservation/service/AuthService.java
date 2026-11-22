package com.example.ticketreservation.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ticketreservation.dto.AuthResponse;
import com.example.ticketreservation.dto.LoginRequest;
import com.example.ticketreservation.dto.RegisterRequest;
import com.example.ticketreservation.dto.UserResponse;
import com.example.ticketreservation.exception.ApiException;
import com.example.ticketreservation.model.AppUser;
import com.example.ticketreservation.model.Role;
import com.example.ticketreservation.repository.AppUserRepository;
import com.example.ticketreservation.security.JwtService;

import lombok.RequiredArgsConstructor;

/**
 * Login, register, me, list users.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Anyone may register as CUSTOMER. Only a logged-in ADMIN may create ADMIN or ORGANIZER accounts.
     * auth is null when no token was sent.
     */
    public UserResponse register(RegisterRequest req, Authentication auth) {
        Role role = (req.role() == null) ? Role.CUSTOMER : req.role();

        if (role != Role.CUSTOMER && !isAdmin(auth)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only an ADMIN can create ADMIN or ORGANIZER accounts");
        }
        if (appUserRepository.existsByUsername(req.username())) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already taken: " + req.username());
        }

        AppUser user = new AppUser(
                null,                                       // id - MongoDB fills it in
                req.username(),
                passwordEncoder.encode(req.password()),     // store the BCrypt hash, never the plain text
                req.fullName(),
                role);

        return UserResponse.from(appUserRepository.save(user));
    }

    /**
     * Checks the password, then issues a JWT.
     * A wrong username or password throws BadCredentialsException, which
     * GlobalExceptionHandler turns into a 401 JSON response.
     */
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));

        AppUser user = appUserRepository.findByUsername(req.username())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        String token = jwtService.generateToken(user);

        return new AuthResponse(token, "Bearer", jwtService.getExpirationMs(),
                user.getUsername(), user.getFullName(), user.getRole());
    }

    public UserResponse me(String username) {
        return appUserRepository.findByUsername(username)
                .map(UserResponse::from)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found: " + username));
    }

    public List<UserResponse> listUsers() {
        return appUserRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
