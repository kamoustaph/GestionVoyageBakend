package moustapha.teach.gestionVoyage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moustapha.teach.gestionVoyage.enums.StatutVoyage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoyageFilterDto {

    private String lieuDepart;
    private String lieuArrivee;
    private StatutVoyage statut;
    private BigDecimal prixMin;
    private BigDecimal prixMax;
    private LocalDateTime dateDepart;
    private LocalDateTime dateRetour;
    private Integer placesMin;

    private String sortBy = "id";
    private String sortDirection = "asc";

    private int page = 0;
    private int size = 10;
}