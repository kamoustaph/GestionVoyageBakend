package moustapha.teach.gestionVoyage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moustapha.teach.gestionVoyage.enums.StatutTicket;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketFilterDto {

    private Long reservationId;
    private Long userId;
    private Long voyageId;
    private StatutTicket statut;
    private LocalDateTime createdAtStart;
    private LocalDateTime createdAtEnd;
    private String qrCode;

    private String sortBy = "id";
    private String sortDirection = "asc";

    private int page = 0;
    private int size = 10;
}