package com.testai.endpointservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité représentant un endpoint d'un service
 */
@Entity
@Table(name = "endpoints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * ID du projet/service auquel appartient cet endpoint
     */
    @Column(nullable = false)
    private UUID projectId;

    /**
     * Méthode HTTP (GET, POST, PUT, DELETE, PATCH)
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private HttpMethod method;

    /**
     * Chemin de l'endpoint (ex: /api/users/{id})
     */
    @Column(nullable = false, length = 500)
    private String path;

    /**
     * Description de l'endpoint
     */
    @Column(length = 1000)
    private String description;

    /**
     * Type de découverte (SWAGGER ou MANUAL)
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscoveryType discoveryType;

    /**
     * Tags associés (ex: "User Management", "Authentication")
     */
    @Column(length = 500)
    private String tags;

    /**
     * Paramètres de requête (JSON)
     */
    @Column(columnDefinition = "TEXT")
    private String parameters;

    /**
     * Corps de requête exemple (JSON)
     */
    @Column(columnDefinition = "TEXT")
    private String requestBody;

    /**
     * Réponse exemple (JSON)
     */
    @Column(columnDefinition = "TEXT")
    private String responseBody;

    /**
     * Codes de statut possibles (ex: "200,201,400,404")
     */
    @Column(length = 200)
    private String statusCodes;

    /**
     * Indique si l'endpoint nécessite une authentification
     */
    @Column(nullable = false)
    private Boolean requiresAuth = false;

    /**
     * Date de création
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date de dernière modification
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Méthodes HTTP supportées
     */
    public enum HttpMethod {
        GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD
    }

    /**
     * Type de découverte de l'endpoint
     */
    public enum DiscoveryType {
        SWAGGER,  // Découvert automatiquement via Swagger/OpenAPI
        MANUAL    // Ajouté manuellement par l'utilisateur
    }
}