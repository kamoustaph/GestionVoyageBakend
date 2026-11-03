package moustapha.teach.gestionVoyage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Contenu lu dans le QR code (ou code du billet saisi à la main).
 * {@code voyageId} optionnel : si l'agent contrôle un départ précis, un billet d'un autre départ est refusé.
 */
public record ControleRequest(@NotBlank @Size(max = 500) String contenu, Long voyageId) {
}
