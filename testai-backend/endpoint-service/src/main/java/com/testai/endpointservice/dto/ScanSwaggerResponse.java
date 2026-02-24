package com.testai.endpointservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour la réponse du scan Swagger
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanSwaggerResponse {
    private boolean success;
    private String message;
    private int totalEndpoints;
    private int newEndpoints;
    private int updatedEndpoints;
    private int skippedEndpoints;
    private List<EndpointDTO> endpoints;
}