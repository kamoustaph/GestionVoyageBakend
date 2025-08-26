package moustapha.teach.gestionVoyage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moustapha.teach.gestionVoyage.enums.StatutVoyage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoyageDto {
    private Long id;
    private String lieuDepart;
    private String lieuArrivee;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateDepart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateRetour;

    private BigDecimal prix;
    private Integer placesDisponibles;
    private Integer placesTotal;
    private StatutVoyage statut;
}