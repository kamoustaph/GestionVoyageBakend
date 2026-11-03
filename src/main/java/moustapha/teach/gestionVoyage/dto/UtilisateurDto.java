package moustapha.teach.gestionVoyage.dto;

import moustapha.teach.gestionVoyage.domain.Role;
import moustapha.teach.gestionVoyage.domain.Utilisateur;

import java.time.LocalDateTime;

public record UtilisateurDto(Long id, String email, String nomComplet, String telephone,
                             Role role, boolean actif, LocalDateTime creeLe) {
    public static UtilisateurDto of(Utilisateur u) {
        return new UtilisateurDto(u.getId(), u.getEmail(), u.getNomComplet(), u.getTelephone(),
                u.getRole(), u.isActif(), u.getCreeLe());
    }
}
