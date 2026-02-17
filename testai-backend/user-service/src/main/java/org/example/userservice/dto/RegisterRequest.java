package org.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO pour la requête d'inscription
 * Email OBLIGATOIRE
 * Téléphone OPTIONNEL (pour quand la vérification SMS sera disponible)
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String name;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password;

    // ⭐️ OPTIONNEL : Numéro de téléphone (pour future vérification SMS)
    // Retirez @NotBlank pour le rendre optionnel
    private String phoneNumber; // Format : +33612345678 ou 0612345678

    private String company;

    private String role; // "ADMIN", "MANAGER", ou "DEVELOPER"
}