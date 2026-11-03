package moustapha.teach.gestionVoyage.dto;

import moustapha.teach.gestionVoyage.domain.StatutBillet;

import java.time.LocalDateTime;

/** Ligne de la liste d'embarquement d'un départ. */
public record PassagerManifesteDto(String code, int siege, String nomPassager, String reservationReference,
                                   String telephone, StatutBillet statut, LocalDateTime embarqueLe) {
}
