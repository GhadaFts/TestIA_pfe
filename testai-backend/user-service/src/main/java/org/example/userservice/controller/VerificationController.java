package org.example.userservice.controller;

import org.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur pour la vérification email ET téléphone
 *
 * ⭐️ AMÉLIORATIONS :
 * - Gestion d'erreurs robuste
 * - Logs détaillés
 * - Pas de @CrossOrigin (géré dans SecurityConfig)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class VerificationController {

    private final UserService userService;

    /**
     * Vérifier l'email via le lien
     * GET /api/auth/verify-email?token=xxx
     */
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam(required = false) String token) {
        log.info("📧 Requête de vérification email reçue");

        // ⭐️ Validation du token
        if (token == null || token.trim().isEmpty()) {
            log.error("❌ Token manquant dans la requête");
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "❌ Token de vérification manquant"
            ));
        }

        log.info("🔍 Vérification avec token: {}...", token.substring(0, Math.min(8, token.length())));

        try {
            Map<String, Object> response = userService.verifyEmailAndActivate(token);

            log.info("✅ Email vérifié avec succès");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Erreur vérification email: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage(),
                    "emailVerified", false,
                    "phoneVerified", false,
                    "accountActive", false
            ));

        } catch (Exception e) {
            log.error("❌ Erreur inattendue lors de la vérification: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Une erreur inattendue s'est produite. Veuillez réessayer.",
                    "error", e.getClass().getSimpleName()
            ));
        }
    }

    /**
     * Vérifier le code SMS
     * POST /api/auth/verify-phone
     */
    @PostMapping("/verify-phone")
    public ResponseEntity<?> verifyPhone(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        log.info("📱 Requête de vérification téléphone pour email: {}", email);

        // Validation des paramètres
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "❌ Email requis"
            ));
        }

        if (code == null || code.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "❌ Code de vérification requis"
            ));
        }

        try {
            Map<String, Object> response = userService.verifyPhoneNumber(email, code);

            log.info("✅ Téléphone vérifié avec succès pour {}", email);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Erreur vérification téléphone: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));

        } catch (Exception e) {
            log.error("❌ Erreur inattendue vérification téléphone: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Une erreur inattendue s'est produite"
            ));
        }
    }

    /**
     * Renvoyer l'email de vérification
     * POST /api/auth/resend-email-verification
     */
    @PostMapping("/resend-email-verification")
    public ResponseEntity<?> resendEmailVerification(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        log.info("📧 Demande de renvoi d'email pour: {}", email);

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "❌ Email requis"
            ));
        }

        try {
            userService.resendVerificationEmail(email);

            log.info("✅ Email de vérification renvoyé à {}", email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "📧 Un nouvel email de vérification a été envoyé à " + email
            ));

        } catch (RuntimeException e) {
            log.error("❌ Erreur renvoi email: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));

        } catch (Exception e) {
            log.error("❌ Erreur inattendue renvoi email: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Impossible d'envoyer l'email"
            ));
        }
    }

    /**
     * Renvoyer le code SMS
     * POST /api/auth/resend-phone-verification
     */
    @PostMapping("/resend-phone-verification")
    public ResponseEntity<?> resendPhoneVerification(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        log.info("📱 Demande de renvoi de code SMS pour: {}", email);

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "❌ Email requis"
            ));
        }

        try {
            userService.resendPhoneVerificationCode(email);

            log.info("✅ Code SMS renvoyé pour {}", email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "📱 Un nouveau code de vérification a été envoyé"
            ));

        } catch (RuntimeException e) {
            log.error("❌ Erreur renvoi code: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));

        } catch (Exception e) {
            log.error("❌ Erreur inattendue renvoi code: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Impossible d'envoyer le SMS"
            ));
        }
    }
}