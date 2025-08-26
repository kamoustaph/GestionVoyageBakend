package moustapha.teach.gestionVoyage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moustapha.teach.gestionVoyage.enums.MethodePaiement;
import moustapha.teach.gestionVoyage.enums.StatutPaiement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaiementFilterDto {

    private Long reservationId;
    private Long userId;
    private StatutPaiement statut;
    private MethodePaiement methode;
    private BigDecimal montantMin;
    private BigDecimal montantMax;
    private LocalDateTime datePaiementStart;
    private LocalDateTime datePaiementEnd;

    private String sortBy = "id";
    private String sortDirection = "asc";

    private int page = 0;
    private int size = 10;
}