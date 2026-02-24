package com.testai.endpointservice.dto;

import com.testai.endpointservice.entity.Endpoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO pour retourner un endpoint
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointDTO {
    private UUID id;
    private UUID projectId;
    private Endpoint.HttpMethod method;
    private String path;
    private String description;
    private Endpoint.DiscoveryType discoveryType;
    private String tags;
    private String parameters;
    private String requestBody;
    private String responseBody;
    private String statusCodes;
    private Boolean requiresAuth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
