package com.example.ticketreservation.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document( collection = "users")
@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class AppUser {

    @Id 
    private String id;
    @Indexed(unique = true)
    private String username;
    private String password;
    private String fullName;
    private Role role;

}
