package org.example.userservice.controller;

import org.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur pour la vérification email ET téléphone
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class VerificationController {

    private final UserService userService;

    /**
     * Vérifier l'email via le lien
     * GET /api/auth/verify-email?token=xxx
     */
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        log.info("Requête de vérification email reçue avec token: {}", token);

        try {
            Map<String, Object> response = userService.verifyEmailAndActivate(token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur vérification email: {}", e.getMessage());

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "❌ " + e.getMessage()
            ));
        }
    }

    /**
     * ⭐️ NOUVEAU : Vérifier le code SMS
     * POST /api/auth/verify-phone
     */
    @PostMapping("/verify-phone")
    public ResponseEntity<?> verifyPhone(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Email requis"
            ));
        }

        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Code de vérification requis"
            ));
        }

        try {
            Map<String, Object> response = userService.verifyPhoneNumber(email, code);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur vérification téléphone: {}", e.getMessage());

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "❌ " + e.getMessage()
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

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Email requis"
            ));
        }

        try {
            userService.resendVerificationEmail(email);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "📧 Un nouvel email de vérification a été envoyé à " + email
            ));

        } catch (Exception e) {
            log.error("Erreur renvoi email: {}", e.getMessage());

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "❌ " + e.getMessage()
            ));
        }
    }

    /**
     * ⭐️ NOUVEAU : Renvoyer le code SMS
     * POST /api/auth/resend-phone-verification
     */
    @PostMapping("/resend-phone-verification")
    public ResponseEntity<?> resendPhoneVerification(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Email requis"
            ));
        }

        try {
            userService.resendPhoneVerificationCode(email);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "📱 Un nouveau code de vérification a été envoyé"
            ));

        } catch (Exception e) {
            log.error("Erreur renvoi code: {}", e.getMessage());

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "❌ " + e.getMessage()
            ));
        }
    }
}