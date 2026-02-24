package com.testai.endpointservice.service;

import com.testai.endpointservice.dto.CreateEndpointRequest;
import com.testai.endpointservice.dto.EndpointDTO;
import com.testai.endpointservice.entity.Endpoint;
import com.testai.endpointservice.repository.EndpointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service principal pour gérer les endpoints
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EndpointService {

    private final EndpointRepository endpointRepository;

    /**
     * Créer un endpoint manuellement
     */
    @Transactional
    public EndpointDTO createEndpoint(CreateEndpointRequest request) {
        log.info("📝 Création d'un endpoint manuel : {} {}", request.getMethod(), request.getPath());

        // Vérifier si l'endpoint existe déjà
        boolean exists = endpointRepository.existsByProjectIdAndMethodAndPath(
                request.getProjectId(),
                request.getMethod(),
                request.getPath()
        );

        if (exists) {
            throw new RuntimeException("Cet endpoint existe déjà pour ce projet");
        }

        Endpoint endpoint = Endpoint.builder()
                .projectId(request.getProjectId())
                .method(request.getMethod())
                .path(request.getPath())
                .description(request.getDescription())
                .discoveryType(Endpoint.DiscoveryType.MANUAL)
                .tags(request.getTags())
                .parameters(request.getParameters())
                .requestBody(request.getRequestBody())
                .responseBody(request.getResponseBody())
                .statusCodes(request.getStatusCodes() != null ? request.getStatusCodes() : "200")
                .requiresAuth(request.getRequiresAuth() != null ? request.getRequiresAuth() : false)
                .build();

        Endpoint saved = endpointRepository.save(endpoint);
        log.info("✅ Endpoint créé avec succès : {}", saved.getId());

        return convertToDTO(saved);
    }

    /**
     * Récupérer tous les endpoints d'un projet
     */
    public List<EndpointDTO> getEndpointsByProjectId(UUID projectId) {
        log.info("📋 Récupération des endpoints du projet {}", projectId);

        return endpointRepository.findByProjectId(projectId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un endpoint par son ID
     */
    public EndpointDTO getEndpointById(UUID endpointId) {
        log.info("🔍 Récupération de l'endpoint {}", endpointId);

        Endpoint endpoint = endpointRepository.findById(endpointId)
                .orElseThrow(() -> new RuntimeException("Endpoint non trouvé"));

        return convertToDTO(endpoint);
    }

    /**
     * Récupérer tous les endpoints
     */
    public List<EndpointDTO> getAllEndpoints() {
        log.info("📋 Récupération de tous les endpoints");

        return endpointRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les endpoints par méthode HTTP
     */
    public List<EndpointDTO> getEndpointsByMethod(Endpoint.HttpMethod method) {
        log.info("📋 Récupération des endpoints avec la méthode {}", method);

        return endpointRepository.findByMethod(method)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les endpoints par type de découverte
     */
    public List<EndpointDTO> getEndpointsByDiscoveryType(Endpoint.DiscoveryType discoveryType) {
        log.info("📋 Récupération des endpoints découverts par {}", discoveryType);

        return endpointRepository.findByDiscoveryType(discoveryType)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour un endpoint
     */
    @Transactional
    public EndpointDTO updateEndpoint(UUID endpointId, CreateEndpointRequest request) {
        log.info("✏️ Mise à jour de l'endpoint {}", endpointId);

        Endpoint endpoint = endpointRepository.findById(endpointId)
                .orElseThrow(() -> new RuntimeException("Endpoint non trouvé"));

        // Mise à jour des champs
        endpoint.setMethod(request.getMethod());
        endpoint.setPath(request.getPath());
        endpoint.setDescription(request.getDescription());
        endpoint.setTags(request.getTags());
        endpoint.setParameters(request.getParameters());
        endpoint.setRequestBody(request.getRequestBody());
        endpoint.setResponseBody(request.getResponseBody());
        endpoint.setStatusCodes(request.getStatusCodes());
        endpoint.setRequiresAuth(request.getRequiresAuth());

        Endpoint updated = endpointRepository.save(endpoint);
        log.info("✅ Endpoint mis à jour avec succès");

        return convertToDTO(updated);
    }

    /**
     * Supprimer un endpoint
     */
    @Transactional
    public void deleteEndpoint(UUID endpointId) {
        log.info("🗑️ Suppression de l'endpoint {}", endpointId);

        if (!endpointRepository.existsById(endpointId)) {
            throw new RuntimeException("Endpoint non trouvé");
        }

        endpointRepository.deleteById(endpointId);
        log.info("✅ Endpoint supprimé avec succès");
    }

    /**
     * Supprimer tous les endpoints d'un projet
     */
    @Transactional
    public void deleteEndpointsByProjectId(UUID projectId) {
        log.info("🗑️ Suppression de tous les endpoints du projet {}", projectId);

        endpointRepository.deleteByProjectId(projectId);
        log.info("✅ Endpoints supprimés avec succès");
    }

    /**
     * Compter les endpoints d'un projet
     */
    public long countEndpointsByProjectId(UUID projectId) {
        return endpointRepository.countByProjectId(projectId);
    }

    /**
     * Convertir Endpoint en DTO
     */
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
}