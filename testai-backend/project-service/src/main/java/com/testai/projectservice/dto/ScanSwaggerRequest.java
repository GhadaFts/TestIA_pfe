package com.testai.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO pour demander un scan Swagger à endpoint-service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanSwaggerRequest {
    private UUID projectId;
    private String swaggerUrl;
}
