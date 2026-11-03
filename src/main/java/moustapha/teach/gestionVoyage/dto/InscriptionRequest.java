package moustapha.teach.gestionVoyage.dto;

import jakarta.validation.constraints.*;

public record InscriptionRequest(
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(min = 2, max = 120) String nomComplet,
        @Pattern(regexp = "^(\\+221)?\\s?[0-9 ]{9,12}$", message = "Numéro sénégalais attendu, ex. 77 123 45 67") String telephone,
        @NotBlank @Size(min = 8, max = 100, message = "8 caractères minimum") String motDePasse) {
}
