package moustapha.teach.gestionVoyage.dto;

import jakarta.validation.constraints.NotNull;
import moustapha.teach.gestionVoyage.domain.MethodePaiement;

public record PaiementRequest(@NotNull MethodePaiement methode, String telephone) {
}
