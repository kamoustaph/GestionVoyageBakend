package moustapha.teach.gestionVoyage.dto;

import moustapha.teach.gestionVoyage.domain.StatutReservation;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationDto(Long id, String reference, StatutReservation statut, int nombrePlaces, int montantTotal,
                             LocalDateTime creeLe, LocalDateTime expireLe, VoyageDto voyage,
                             String client, String clientEmail, PaiementDto paiement, List<BilletDto> billets) {
}
