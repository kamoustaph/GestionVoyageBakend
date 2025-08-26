package moustapha.teach.gestionVoyage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moustapha.teach.gestionVoyage.enums.ReservationStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDetailDto {
    private Long id;
    private Long userId;
    private String userNom;
    private String userPrenom;
    private String userEmail;
    private Long voyageId;
    private String voyageLieuDepart;
    private String voyageLieuArrivee;
    private LocalDateTime voyageDateDepart;
    private LocalDateTime voyageDateRetour;
    private ReservationStatus statut;
    private LocalDateTime dateReservation;
    private LocalDateTime dateExpiration;
    private Integer nombrePlaces;
    private Double prixTotal;
    private Double voyagePrix;


    public void setVoyagePrix(Double voyagePrix) {
        this.voyagePrix = voyagePrix;
        if (this.nombrePlaces != null && voyagePrix != null) {
            this.prixTotal = voyagePrix * this.nombrePlaces;
        }
    }
}