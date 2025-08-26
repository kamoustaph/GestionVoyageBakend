package moustapha.teach.gestionVoyage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moustapha.teach.gestionVoyage.enums.StatutTicket;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketDetailDto {
    private Long id;
    private Long reservationId;
    private Long userId;
    private String userNom;
    private String userPrenom;
    private String userEmail;
    private Long voyageId;
    private String voyageLieuDepart;
    private String voyageLieuArrivee;
    private LocalDateTime voyageDateDepart;
    private LocalDateTime voyageDateRetour;
    private String qrCode;
    private StatutTicket statut;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}