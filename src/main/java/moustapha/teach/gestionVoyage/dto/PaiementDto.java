package moustapha.teach.gestionVoyage.dto;

import moustapha.teach.gestionVoyage.domain.MethodePaiement;

import java.time.LocalDateTime;

public record PaiementDto(int montant, MethodePaiement methode, String telephone, String transactionRef,
                          LocalDateTime payeLe) {
}
