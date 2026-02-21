package org.example.userservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ========================================
                        // ENDPOINTS PUBLICS (sans authentification)
                        // ========================================
                        .requestMatchers(
                                // ⭐️ API Auth - TOUS LES ENDPOINTS
                                "/api/auth/**",

                                // ⭐️ CRITIQUE : Ajouter /error pour Spring Boot
                                "/error",
                                "/error/**",

                                // Actuator
                                "/actuator/health",
                                "/actuator/health/**",
                                "/actuator/info",

                                // Swagger / OpenAPI
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**"
                        ).permitAll()

                        // ========================================
                        // ENDPOINTS PROTÉGÉS
                        // ========================================
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/services/**").hasAnyRole("ADMIN", "MANAGER")

                        // ========================================
                        // TOUS LES AUTRES ENDPOINTS
                        // ========================================
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> {})
                );

        return http.build();
    }

    /**
     * ⭐️ Configuration CORS centralisée (SIMPLIFIÉE)
     * Pas besoin de dupliquer dans WebConfig
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ⭐️ Origines autorisées
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://localhost:4173"
        ));

        // ⭐️ Méthodes HTTP
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // ⭐️ Headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // ⭐️ Credentials
        configuration.setAllowCredentials(true);

        // ⭐️ Max age
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}