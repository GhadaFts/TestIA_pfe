package org.example.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    /**
     * Créer un utilisateur dans Keycloak avec un rôle
     */
    public String createUser(String email, String password, String name, String role) {
        try {
            String adminToken = getAdminToken();
            String url = keycloakUrl + "/admin/realms/" + realm + "/users";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(adminToken);

            Map<String, Object> user = new HashMap<>();
            user.put("username", email);
            user.put("email", email);
            user.put("firstName", name.split(" ")[0]);
            user.put("lastName", name.contains(" ") ? name.substring(name.indexOf(" ") + 1) : "");
            user.put("enabled", true);
            user.put("emailVerified", true);

            // Utiliser une liste de credentials (Keycloak attend un array)
            Map<String, Object> credential = new HashMap<>();
            credential.put("type", "password");
            credential.put("value", password);
            credential.put("temporary", Boolean.FALSE);

            user.put("credentials", List.of(credential));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode() != HttpStatus.CREATED) {
                log.error("❌ Création utilisateur Keycloak échouée : status={}, body={}", response.getStatusCodeValue(), response.getBody());
                throw new RuntimeException("Échec création utilisateur Keycloak: status=" + response.getStatusCodeValue());
            }

            // Récupérer l'ID de l'utilisateur créé (Location header)
            String location = response.getHeaders().getLocation() != null ? response.getHeaders().getLocation().toString() : null;
            if (location == null) {
                log.error("❌ Location header absent après création d'utilisateur : headers={}", response.getHeaders());
                throw new RuntimeException("Location header manquant après création d'utilisateur");
            }

            String userId = location.substring(location.lastIndexOf('/') + 1);

            log.info("✅ Utilisateur créé dans Keycloak avec l'ID: {}", userId);

            // Assigner le rôle (monitorer erreurs)
            assignRoleToUser(userId, role, adminToken);

            return userId;

        } catch (HttpClientErrorException e) {
            log.error("❌ Erreur HTTP lors de la création d'utilisateur Keycloak: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Impossible de créer l'utilisateur dans Keycloak: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("❌ Erreur lors de la création de l'utilisateur dans Keycloak: {}", e.getMessage());
            throw new RuntimeException("Impossible de créer l'utilisateur dans Keycloak", e);
        }
    }

    /**
     * ⭐️ NOUVEAU : Assigner un rôle à un utilisateur
     */
    /**
     * ✅ VERSION CORRIGÉE : Assigner un rôle à un utilisateur
     */
    private void assignRoleToUser(String userId, String roleName, String adminToken) {
        try {
            log.info("🔍 Tentative d'assignation du rôle '{}' à l'utilisateur {}", roleName, userId);

            // 1. Récupérer le rôle realm par son nom
            String getRoleUrl = keycloakUrl + "/admin/realms/" + realm + "/roles/" + roleName;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(adminToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> getRequest = new HttpEntity<>(headers);

            ResponseEntity<Map> roleResponse = restTemplate.exchange(
                    getRoleUrl,
                    HttpMethod.GET,
                    getRequest,
                    Map.class
            );

            if (roleResponse.getStatusCode() != HttpStatus.OK || roleResponse.getBody() == null) {
                log.error("❌ Rôle '{}' non trouvé (status={})", roleName, roleResponse.getStatusCodeValue());
                throw new RuntimeException("Rôle '" + roleName + "' non trouvé dans Keycloak");
            }

            Map<String, Object> roleData = roleResponse.getBody();
            log.info("📦 Rôle trouvé: id={}, name={}", roleData.get("id"), roleData.get("name"));

            // 2. Préparer la liste des rôles à assigner
            List<Map<String, Object>> rolesToAssign = List.of(roleData);

            // 3. Assigner le rôle à l'utilisateur
            String assignRoleUrl = keycloakUrl + "/admin/realms/" + realm
                    + "/users/" + userId + "/role-mappings/realm";

            HttpEntity<List<Map<String, Object>>> assignRequest = new HttpEntity<>(rolesToAssign, headers);

            ResponseEntity<String> assignResp = restTemplate.exchange(
                    assignRoleUrl,
                    HttpMethod.POST,
                    assignRequest,
                    String.class
            );

            if (!(assignResp.getStatusCode() == HttpStatus.NO_CONTENT || assignResp.getStatusCode() == HttpStatus.OK)) {
                log.error("❌ Échec assignation rôle: status={}, body={}", assignResp.getStatusCodeValue(), assignResp.getBody());
                throw new RuntimeException("Impossible d'assigner le rôle: status=" + assignResp.getStatusCodeValue());
            }

            log.info("✅ Rôle '{}' assigné avec succès à l'utilisateur {}", roleName, userId);

        } catch (HttpClientErrorException e) {
            log.error("❌ Erreur HTTP lors de l'assignation du rôle '{}': status={}, body={}", roleName, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Impossible d'assigner le rôle: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'assignation du rôle '{}': {}", roleName, e.getMessage());
            throw new RuntimeException("Impossible d'assigner le rôle: " + e.getMessage(), e);
        }
    }

    /**
     * Authentifier un utilisateur et obtenir un token
     */
    public Map<String, Object> authenticateUser(String email, String password) {
        try {
            String url = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("grant_type", "password");
            body.add("username", email);
            body.add("password", password);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            log.info("✅ Authentification réussie pour l'utilisateur: {}", email);
            return response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("❌ Erreur d'authentification Keycloak: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Identifiants invalides: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'authentification: {}", e.getMessage());
            throw new RuntimeException("Identifiants invalides", e);
        }
    }

    /**
     * Obtenir un token admin
     */
    private String getAdminToken() {
        try {
            String url = keycloakUrl + "/realms/master/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", "admin-cli");
            body.add("grant_type", "password");
            body.add("username", "admin");
            body.add("password", "admin123");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null || response.getBody().get("access_token") == null) {
                log.error("❌ Impossible d'obtenir token admin: status={}, body={}", response.getStatusCodeValue(), response.getBody());
                throw new RuntimeException("Impossible d'obtenir le token admin: " + response.getStatusCodeValue());
            }

            return (String) response.getBody().get("access_token");

        } catch (HttpClientErrorException e) {
            log.error("❌ Erreur HTTP lors de l'obtention du token admin: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Impossible d'obtenir le token admin: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'obtention du token admin: {}", e.getMessage());
            throw new RuntimeException("Impossible d'obtenir le token admin", e);
        }
    }

    /**
     * Rafraîchir un access token
     */
    public Map<String, Object> refreshToken(String refreshToken) {
        try {
            String url = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("grant_type", "refresh_token");
            body.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("❌ Erreur lors du rafraîchissement du token: {}", e.getMessage());
            throw new RuntimeException("Impossible de rafraîchir le token", e);
        }
    }
}