package com.testai.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO pour recevoir les données utilisateur depuis user-service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private String role;
    private Boolean isActive;
    private String company;
    private String phoneNumber;
}