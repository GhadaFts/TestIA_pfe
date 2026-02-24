package com.testai.endpointservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testai.endpointservice.dto.EndpointDTO;
import com.testai.endpointservice.dto.ScanSwaggerResponse;
import com.testai.endpointservice.entity.Endpoint;
import com.testai.endpointservice.repository.EndpointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Service pour scanner les endpoints depuis Swagger/OpenAPI
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SwaggerScannerService {

    private final EndpointRepository endpointRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Scanner une URL Swagger/OpenAPI et extraire les endpoints
     */
    @Transactional
    public ScanSwaggerResponse scanSwagger(UUID projectId, String swaggerUrl) {
        log.info("🔍 Début du scan Swagger pour le projet {} depuis {}", projectId, swaggerUrl);

        try {
            // 1. Télécharger le fichier Swagger JSON
            String swaggerJson = restTemplate.getForObject(swaggerUrl, String.class);

            if (swaggerJson == null || swaggerJson.isEmpty()) {
                return createErrorResponse("Le fichier Swagger est vide");
            }

            // 2. Parser le JSON
            JsonNode rootNode = objectMapper.readTree(swaggerJson);

            // 3. Détecter la version OpenAPI
            String version = detectOpenApiVersion(rootNode);
            log.info("📋 Version OpenAPI détectée : {}", version);

            // 4. Extraire les endpoints
            List<Endpoint> endpoints;
            if (version.startsWith("3.")) {
                endpoints = extractEndpointsFromOpenApi3(projectId, rootNode);
            } else if (version.startsWith("2.")) {
                endpoints = extractEndpointsFromSwagger2(projectId, rootNode);
            } else {
                return createErrorResponse("Version OpenAPI non supportée : " + version);
            }

            // 5. Sauvegarder les endpoints
            ScanSwaggerResponse response = saveEndpoints(projectId, endpoints);

            log.info("✅ Scan terminé : {} endpoints traités", response.getTotalEndpoints());
            return response;

        } catch (Exception e) {
            log.error("❌ Erreur lors du scan Swagger : {}", e.getMessage(), e);
            return createErrorResponse("Erreur : " + e.getMessage());
        }
    }

    /**
     * Détecter la version d'OpenAPI
     */
    private String detectOpenApiVersion(JsonNode rootNode) {
        // OpenAPI 3.x
        if (rootNode.has("openapi")) {
            return rootNode.get("openapi").asText();
        }
        // Swagger 2.x
        if (rootNode.has("swagger")) {
            return rootNode.get("swagger").asText();
        }
        return "unknown";
    }

    /**
     * Extraire les endpoints depuis OpenAPI 3.x
     */
    private List<Endpoint> extractEndpointsFromOpenApi3(UUID projectId, JsonNode rootNode) {
        List<Endpoint> endpoints = new ArrayList<>();

        JsonNode pathsNode = rootNode.get("paths");
        if (pathsNode == null) {
            log.warn("⚠️ Aucun path trouvé dans le fichier OpenAPI");
            return endpoints;
        }

        // Parcourir tous les paths
        Iterator<String> pathIterator = pathsNode.fieldNames();
        while (pathIterator.hasNext()) {
            String path = pathIterator.next();
            JsonNode pathItem = pathsNode.get(path);

            // Parcourir toutes les méthodes HTTP (get, post, put, delete, etc.)
            Iterator<String> methodIterator = pathItem.fieldNames();
            while (methodIterator.hasNext()) {
                String methodStr = methodIterator.next();

                // Ignorer les champs non-méthodes (parameters, servers, etc.)
                if (!isHttpMethod(methodStr)) {
                    continue;
                }

                JsonNode operation = pathItem.get(methodStr);

                // Créer l'endpoint
                Endpoint endpoint = Endpoint.builder()
                        .projectId(projectId)
                        .method(parseHttpMethod(methodStr))
                        .path(path)
                        .description(extractText(operation, "summary", "description"))
                        .discoveryType(Endpoint.DiscoveryType.SWAGGER)
                        .tags(extractTags(operation))
                        .parameters(extractParameters(operation))
                        .requestBody(extractRequestBody(operation))
                        .responseBody(extractResponses(operation))
                        .statusCodes(extractStatusCodes(operation))
                        .requiresAuth(checkIfRequiresAuth(operation))
                        .build();

                endpoints.add(endpoint);
                log.debug("📍 Endpoint trouvé : {} {}", methodStr.toUpperCase(), path);
            }
        }

        return endpoints;
    }

    /**
     * Extraire les endpoints depuis Swagger 2.x
     */
    private List<Endpoint> extractEndpointsFromSwagger2(UUID projectId, JsonNode rootNode) {
        // Swagger 2 a une structure similaire à OpenAPI 3
        return extractEndpointsFromOpenApi3(projectId, rootNode);
    }

    /**
     * Sauvegarder les endpoints et gérer les doublons
     */
    private ScanSwaggerResponse saveEndpoints(UUID projectId, List<Endpoint> endpoints) {
        int newCount = 0;
        int updatedCount = 0;
        int skippedCount = 0;
        List<EndpointDTO> savedEndpoints = new ArrayList<>();

        for (Endpoint endpoint : endpoints) {
            boolean exists = endpointRepository.existsByProjectIdAndMethodAndPath(
                    projectId, endpoint.getMethod(), endpoint.getPath()
            );

            if (exists) {
                skippedCount++;
                log.debug("⏭️ Endpoint ignoré (déjà existant) : {} {}", endpoint.getMethod(), endpoint.getPath());
            } else {
                Endpoint saved = endpointRepository.save(endpoint);
                newCount++;
                savedEndpoints.add(convertToDTO(saved));
                log.debug("✅ Endpoint créé : {} {}", saved.getMethod(), saved.getPath());
            }
        }

        return new ScanSwaggerResponse(
                true,
                "Scan terminé avec succès",
                endpoints.size(),
                newCount,
                updatedCount,
                skippedCount,
                savedEndpoints
        );
    }

    /**
     * Utilitaires d'extraction
     */
    private boolean isHttpMethod(String method) {
        return method.equalsIgnoreCase("get") ||
                method.equalsIgnoreCase("post") ||
                method.equalsIgnoreCase("put") ||
                method.equalsIgnoreCase("delete") ||
                method.equalsIgnoreCase("patch") ||
                method.equalsIgnoreCase("options") ||
                method.equalsIgnoreCase("head");
    }

    private Endpoint.HttpMethod parseHttpMethod(String method) {
        return Endpoint.HttpMethod.valueOf(method.toUpperCase());
    }

    private String extractText(JsonNode node, String... fields) {
        for (String field : fields) {
            if (node.has(field)) {
                return node.get(field).asText();
            }
        }
        return null;
    }

    private String extractTags(JsonNode operation) {
        if (operation.has("tags") && operation.get("tags").isArray()) {
            StringBuilder tags = new StringBuilder();
            operation.get("tags").forEach(tag -> {
                if (tags.length() > 0) tags.append(", ");
                tags.append(tag.asText());
            });
            return tags.toString();
        }
        return null;
    }

    private String extractParameters(JsonNode operation) {
        if (operation.has("parameters")) {
            try {
                return objectMapper.writeValueAsString(operation.get("parameters"));
            } catch (Exception e) {
                log.warn("Erreur lors de l'extraction des paramètres : {}", e.getMessage());
            }
        }
        return null;
    }

    private String extractRequestBody(JsonNode operation) {
        if (operation.has("requestBody")) {
            try {
                return objectMapper.writeValueAsString(operation.get("requestBody"));
            } catch (Exception e) {
                log.warn("Erreur lors de l'extraction du requestBody : {}", e.getMessage());
            }
        }
        return null;
    }

    private String extractResponses(JsonNode operation) {
        if (operation.has("responses")) {
            try {
                // Extraire seulement les réponses 2xx et 200
                JsonNode responses = operation.get("responses");
                JsonNode successResponse = responses.has("200") ? responses.get("200") :
                        responses.has("201") ? responses.get("201") :
                                null;
                if (successResponse != null) {
                    return objectMapper.writeValueAsString(successResponse);
                }
            } catch (Exception e) {
                log.warn("Erreur lors de l'extraction des réponses : {}", e.getMessage());
            }
        }
        return null;
    }

    private String extractStatusCodes(JsonNode operation) {
        if (operation.has("responses")) {
            StringBuilder codes = new StringBuilder();
            Iterator<String> codeIterator = operation.get("responses").fieldNames();
            while (codeIterator.hasNext()) {
                String code = codeIterator.next();
                if (codes.length() > 0) codes.append(",");
                codes.append(code);
            }
            return codes.toString();
        }
        return "200";
    }

    private Boolean checkIfRequiresAuth(JsonNode operation) {
        // Vérifier si l'endpoint nécessite une authentification
        if (operation.has("security") && operation.get("security").size() > 0) {
            return true;
        }
        return false;
    }

    private EndpointDTO convertToDTO(Endpoint endpoint) {
        return EndpointDTO.builder()
                .id(endpoint.getId())
                .projectId(endpoint.getProjectId())
                .method(endpoint.getMethod())
                .path(endpoint.getPath())
                .description(endpoint.getDescription())
                .discoveryType(endpoint.getDiscoveryType())
                .tags(endpoint.getTags())
                .parameters(endpoint.getParameters())
                .requestBody(endpoint.getRequestBody())
                .responseBody(endpoint.getResponseBody())
                .statusCodes(endpoint.getStatusCodes())
                .requiresAuth(endpoint.getRequiresAuth())
                .createdAt(endpoint.getCreatedAt())
                .updatedAt(endpoint.getUpdatedAt())
                .build();
    }

    private ScanSwaggerResponse createErrorResponse(String message) {
        return new ScanSwaggerResponse(false, message, 0, 0, 0, 0, new ArrayList<>());
    }
}