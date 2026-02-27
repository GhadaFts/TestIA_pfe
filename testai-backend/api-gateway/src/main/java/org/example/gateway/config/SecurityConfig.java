package org.example.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration de sécurité pour API Gateway avec CORS
 *
 * IMPORTANT : CORS est configuré dans CorsGlobalConfiguration.java
 * Cette classe gère uniquement l'authentification JWT
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:http://localhost:8080/realms/testai}")
    private String issuerUri;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                // ========================================
                // CSRF : Désactivé (API stateless)
                // ========================================
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // ========================================
                // CORS : Géré par CorsGlobalConfiguration
                // ========================================
                // Pas besoin de configurer CORS ici
                // La configuration dans CorsGlobalConfiguration.java
                // est automatiquement appliquée

                // ========================================
                // AUTORISATIONS
                // ========================================
                .authorizeExchange(exchanges -> exchanges
                        // Endpoints publics (pas d'authentification requise)
                        .pathMatchers("/api/auth/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/actuator/health/**").permitAll()

                        // OPTIONS : Toujours autoriser (preflight CORS)
                        .pathMatchers("OPTIONS", "/**").permitAll()

                        // Tous les autres endpoints nécessitent une authentification
                        .anyExchange().authenticated()
                )

                // ========================================
                // OAUTH2 RESOURCE SERVER (JWT)
                // ========================================
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                            // Configuration JWT automatique via issuer-uri
                        })
                );

        return http.build();
    }
}
