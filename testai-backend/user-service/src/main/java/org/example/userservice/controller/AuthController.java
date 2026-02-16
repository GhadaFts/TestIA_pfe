package org.example.userservice.controller;

import org.example.userservice.dto.*;
import org.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    /**
     * ✅ CORRIGÉ : Inscription avec vérification email
     * Retourne un message demandant de vérifier l'email (pas de token)
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Requête d'inscription reçue pour: {}", request.getEmail());

        try {
            Map<String, Object> response = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Erreur lors de l'inscription: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "❌ " + e.getMessage()
            ));
        }
    }

    /**
     * Connexion
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("Requête de connexion reçue pour: {}", request.getEmail());

        try {
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de la connexion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "❌ " + e.getMessage()
            ));
        }
    }

    /**
     * Rafraîchir le token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Refresh token requis"
            ));
        }

        try {
            AuthResponse response = userService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Token invalide ou expiré"
            ));
        }
    }

    /**
     * Déconnexion (côté client uniquement, supprimer le token)
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie"));
    }

    /**
     * Demander la réinitialisation du mot de passe
     * POST /api/auth/forgot-password
     * Body: { "email": "user@example.com" }
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Demande de réinitialisation de mot de passe pour: {}", request.getEmail());

        try {
            Map<String, Object> response = userService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur demande réinitialisation: {}", e.getMessage());

            // Pour la sécurité, on retourne toujours un succès même si l'email n'existe pas
            // Cela empêche de deviner quels emails sont enregistrés
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "📧 Si cet email existe dans notre système, un lien de réinitialisation a été envoyé."
            ));
        }
    }

    /**
     * Vérifier le token de réinitialisation (pour afficher le formulaire)
     * GET /api/auth/validate-reset-token?token=xxx
     */
    @GetMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        log.info("Validation du token de réinitialisation");

        try {
            Map<String, Object> response = userService.validateResetToken(token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur validation token: {}", e.getMessage());

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "❌ " + e.getMessage()
            ));
        }
    }

    /**
     * Réinitialiser le mot de passe
     * POST /api/auth/reset-password
     * Body: { "token": "xxx", "newPassword": "...", "confirmPassword": "..." }
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Réinitialisation du mot de passe");

        try {
            Map<String, Object> response = userService.resetPassword(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur réinitialisation mot de passe: {}", e.getMessage());

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "❌ " + e.getMessage()
            ));
        }
    }
}