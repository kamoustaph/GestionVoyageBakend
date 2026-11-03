package moustapha.teach.gestionVoyage.dto;

import jakarta.validation.constraints.*;
import moustapha.teach.gestionVoyage.domain.Role;

/** Création d'un compte par un administrateur (agent de contrôle ou autre admin). */
public record CreerUtilisateurRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 2, max = 120) String nomComplet,
        String telephone,
        @NotBlank @Size(min = 8, max = 100) String motDePasse,
        @NotNull Role role) {
}
