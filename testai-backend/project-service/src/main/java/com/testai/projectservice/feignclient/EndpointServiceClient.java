package com.testai.projectservice.feignclient;

import com.testai.projectservice.config.FeignClientConfig;
import com.testai.projectservice.dto.EndpointDTO;
import com.testai.projectservice.dto.ScanSwaggerRequest;
import com.testai.projectservice.dto.ScanSwaggerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Feign Client pour communiquer avec endpoint-service
 *
 * @FeignClient:
 * - name: nom du service dans Eureka (ENDPOINT-SERVICE)
 * - path: préfixe des endpoints (/api/endpoints)
 */
@FeignClient(name = "endpoint-service", path = "/api/endpoints", configuration = FeignClientConfig.class)
public interface EndpointServiceClient {

    /**
     * Scanner les endpoints depuis une URL Swagger
     * POST /api/endpoints/scan
     */
    @PostMapping("/scan")
    ScanSwaggerResponse scanSwagger(@RequestBody ScanSwaggerRequest request);

    /**
     * Récupérer tous les endpoints d'un projet
     * GET /api/endpoints/project/{projectId}
     */
    @GetMapping("/project/{projectId}")
    List<EndpointDTO> getEndpointsByProjectId(@PathVariable("projectId") UUID projectId);

    /**
     * Compter les endpoints d'un projet
     * GET /api/endpoints/project/{projectId}/count
     */
    @GetMapping("/project/{projectId}/count")
    Map<String, Object> countEndpointsByProjectId(@PathVariable("projectId") UUID projectId);

    /**
     * Supprimer tous les endpoints d'un projet
     * DELETE /api/endpoints/project/{projectId}
     */
    @DeleteMapping("/project/{projectId}")
    Map<String, Object> deleteEndpointsByProjectId(@PathVariable("projectId") UUID projectId);
}