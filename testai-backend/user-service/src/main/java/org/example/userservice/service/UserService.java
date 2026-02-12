package org.example.userservice.service;

import org.example.userservice.dto.*;
import org.example.userservice.entity.User;
import org.example.userservice.entity.User.UserRole;
import org.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final TransactionTemplate transactionTemplate;

    /**
     * Inscription d'un nouvel utilisateur
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Tentative d'inscription pour l'email: {}", request.getEmail());

        // 1. Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        // ⭐️ NOUVEAU : Déterminer le rôle (par défaut MANAGER si non spécifié)
        String role = request.getRole();
        if (role == null || role.isEmpty()) {
            role = "MANAGER";
        }

        // ⭐️ NOUVEAU : Valider le rôle
        if (!role.equals("ADMIN") && !role.equals("MANAGER") && !role.equals("DEVELOPER")) {
            throw new RuntimeException("Rôle invalide. Valeurs acceptées: ADMIN, MANAGER, DEVELOPER");
        }

        log.info("📝 Rôle demandé: {}", role);

        // 2. Créer l'utilisateur dans Keycloak AVEC le rôle
        String keycloakId;
        try {
            // ⭐️ MODIFIÉ : Passer le rôle à createUser()
            keycloakId = keycloakService.createUser(
                    request.getEmail(),
                    request.getPassword(),
                    request.getName(),
                    role  // ← Passer le rôle ici
            );
            log.info("✅ Utilisateur créé dans Keycloak avec l'ID: {} et le rôle: {}", keycloakId, role);
        } catch (Exception e) {
            log.error("❌ Erreur création Keycloak: {}", e.getMessage());
            throw new RuntimeException("Impossible de créer l'utilisateur dans Keycloak: " + e.getMessage());
        }

        // 3. Créer l'utilisateur dans PostgreSQL
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // ⭐️ MODIFIÉ : Utiliser le rôle de la requête (converti en enum)
        user.setRole(UserRole.valueOf(role));
        user.setKeycloakId(keycloakId);
        user.setCompany(request.getCompany());
        user.setIsActive(true);

        user = userRepository.save(user);
        log.info("✅ Utilisateur sauvegardé dans PostgreSQL avec l'ID: {} et le rôle: {}", user.getId(), role);

        // 4. Authentifier l'utilisateur
        Map<String, Object> keycloakResponse;
        try {
            keycloakResponse = keycloakService.authenticateUser(
                    request.getEmail(),
                    request.getPassword()
            );
            log.info("✅ Authentification réussie");
        } catch (Exception e) {
            log.error("❌ Erreur authentification: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de l'authentification: " + e.getMessage());
        }

        // 5. Mettre à jour last_login
        user.setLastLogin(Instant.now());
        userRepository.save(user);

        // 6. Construire et retourner la réponse
        UserDTO userDTO = mapToDTO(user);

        return new AuthResponse(
                (String) keycloakResponse.get("access_token"),
                (String) keycloakResponse.get("refresh_token"),
                (Integer) keycloakResponse.get("expires_in"),
                userDTO
        );
    }

    /**
     * Connexion d'un utilisateur
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Tentative de connexion pour l'email: {}", request.getEmail());

        // 1. Récupérer l'utilisateur
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // 2. Vérifier si le compte est actif
        if (!user.getIsActive()) {
            throw new RuntimeException("Compte désactivé");
        }

        // 3. Authentifier via Keycloak
        Map<String, Object> keycloakResponse = keycloakService.authenticateUser(
                request.getEmail(),
                request.getPassword()
        );

        log.info("Authentification réussie pour l'utilisateur: {}", request.getEmail());

        // 4. Mettre à jour la date de dernière connexion
        user.setLastLogin(Instant.now());
        userRepository.save(user);

        // 5. Construire la réponse
        UserDTO userDTO = mapToDTO(user);

        return new AuthResponse(
                (String) keycloakResponse.get("access_token"),
                (String) keycloakResponse.get("refresh_token"),
                (Integer) keycloakResponse.get("expires_in"),
                userDTO
        );
    }

    /**
     * Récupérer un utilisateur par ID
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return mapToDTO(user);
    }

    /**
     * Récupérer un utilisateur par email
     */
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return mapToDTO(user);
    }

    /**
     * Mettre à jour un utilisateur
     */
    @Transactional
    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getCompany() != null) {
            user.setCompany(userDTO.getCompany());
        }
        if (userDTO.getAvatar() != null) {
            user.setAvatar(userDTO.getAvatar());
        }

        user = userRepository.save(user);
        log.info("Utilisateur mis à jour: {}", user.getId());

        return mapToDTO(user);
    }

    /**
     * Rafraîchir le token
     */
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Rafraîchissement du token");

        Map<String, Object> keycloakResponse = keycloakService.refreshToken(refreshToken);

        return new AuthResponse(
                (String) keycloakResponse.get("access_token"),
                (String) keycloakResponse.get("refresh_token"),
                (Integer) keycloakResponse.get("expires_in"),
                null
        );
    }

    /**
     * Mapper User entity vers UserDTO
     */
    private UserDTO mapToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getAvatar(),
                user.getCompany(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getLastLogin()
        );
    }
}