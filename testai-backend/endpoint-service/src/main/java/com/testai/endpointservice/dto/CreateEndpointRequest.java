package com.testai.endpointservice.dto;

import com.testai.endpointservice.entity.Endpoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO pour créer un endpoint manuellement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEndpointRequest {
    private UUID projectId;
    private Endpoint.HttpMethod method;
    private String path;
    private String description;
    private String tags;
    private String parameters;
    private String requestBody;
    private String responseBody;
    private String statusCodes;
    private Boolean requiresAuth;
}