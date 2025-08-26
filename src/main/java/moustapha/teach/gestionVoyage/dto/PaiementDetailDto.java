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
public class PaiementDetailDto {
    private Long id;
    private Long reservationId;
    private Long userId;
    private String userNom;
    private String userPrenom;
    private String userEmail;
    private Long voyageId;
    private String voyageLieuDepart;
    private String voyageLieuArrivee;
    private BigDecimal montant;
    private MethodePaiement methode;
    private StatutPaiement statut;
    private LocalDateTime datePaiement;
    private LocalDateTime updatedAt;
}