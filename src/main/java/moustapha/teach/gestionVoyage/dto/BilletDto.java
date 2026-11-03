package moustapha.teach.gestionVoyage.dto;

import moustapha.teach.gestionVoyage.domain.StatutBillet;

import java.time.LocalDateTime;

/**
 * Billet tel que vu par son propriétaire ou un agent.
 * {@code qrContenu} est la chaîne à encoder dans le QR code (signée par le serveur).
 */
public record BilletDto(Long id, String code, String nomPassager, int siege, StatutBillet statut,
                        String reservationReference, VoyageDto voyage, String qrContenu,
                        LocalDateTime embarqueLe) {
}
