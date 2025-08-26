package moustapha.teach.gestionVoyage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moustapha.teach.gestionVoyage.enums.ReservationStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationFilterDto {

    private Long userId;
    private Long voyageId;
    private ReservationStatus statut;
    private LocalDateTime dateReservationStart;
    private LocalDateTime dateReservationEnd;
    private LocalDateTime dateExpirationStart;
    private LocalDateTime dateExpirationEnd;
    private Integer nombrePlacesMin;
    private Integer nombrePlacesMax;

    private String sortBy = "id";
    private String sortDirection = "asc";

    private int page = 0;
    private int size = 10;
}