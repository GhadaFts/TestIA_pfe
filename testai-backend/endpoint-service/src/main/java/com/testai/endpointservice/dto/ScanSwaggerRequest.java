package com.testai.endpointservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO pour scanner les endpoints depuis Swagger
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanSwaggerRequest {
    private UUID projectId;
    private String swaggerUrl;  // URL du swagger.json ou swagger.yaml
}