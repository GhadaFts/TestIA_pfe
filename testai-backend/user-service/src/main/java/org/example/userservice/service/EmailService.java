package org.example.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // ✅ CORRECTION : Utiliser l'URL du BACKEND, pas du frontend
    @Value("${app.backend-url:http://localhost:8081}")
    private String backendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Envoyer un email de vérification
     */
    public void sendVerificationEmail(String toEmail, String userName, String verificationToken) {
        try {
            // ✅ URL BACKEND pour la vérification (endpoint GET)
            String verificationUrl = backendUrl + "/api/auth/verify-email?token=" + verificationToken;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("TestAI - Vérifiez votre adresse email");
            message.setText(
                    "Bonjour " + userName + ",\n\n" +
                            "Bienvenue sur TestAI !\n\n" +
                            "Pour activer votre compte, veuillez cliquer sur le lien suivant :\n\n" +
                            verificationUrl + "\n\n" +
                            "Ce lien est valable pendant 24 heures.\n\n" +
                            "Si vous n'avez pas créé de compte TestAI, vous pouvez ignorer cet email.\n\n" +
                            "Cordialement,\n" +
                            "L'équipe TestAI"
            );

            mailSender.send(message);
            log.info("✅ Email de vérification envoyé à {} avec URL: {}", toEmail, verificationUrl);

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'email à {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Impossible d'envoyer l'email de vérification", e);
        }
    }

    /**
     * Renvoyer un email de vérification
     */
    public void resendVerificationEmail(String toEmail, String userName, String verificationToken) {
        sendVerificationEmail(toEmail, userName, verificationToken);
        log.info("📧 Email de vérification renvoyé à {}", toEmail);
    }
}