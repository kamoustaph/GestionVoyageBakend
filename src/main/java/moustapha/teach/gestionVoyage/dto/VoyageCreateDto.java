package moustapha.teach.gestionVoyage.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moustapha.teach.gestionVoyage.config.JacksonConfig;
import moustapha.teach.gestionVoyage.enums.StatutVoyage;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoyageCreateDto {

    private Long id;

    @NotBlank(message = "Le lieu de départ est obligatoire")
    private String lieuDepart;

    @NotBlank(message = "Le lieu d'arrivée est obligatoire")
    private String lieuArrivee;

    @NotNull(message = "La date de départ est obligatoire")
    @Future(message = "La date de départ doit être future")
    @JsonDeserialize(using = JacksonConfig.CustomLocalDateTimeDeserializer.class)
    private LocalDateTime dateDepart;

    @NotNull(message = "La date de retour est obligatoire")
    @JsonDeserialize(using = JacksonConfig.CustomLocalDateTimeDeserializer.class)
    private LocalDateTime dateRetour;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être positif")
    private BigDecimal prix;

    @NotNull(message = "Le nombre de places disponibles est obligatoire")
    @Min(value = 0, message = "Le nombre de places disponibles ne peut pas être négatif")
    private Integer placesDisponibles;

    @NotNull(message = "Le nombre total de places est obligatoire")
    @Min(value = 1, message = "Il doit y avoir au moins une place au total")
    private Integer placesTotal;

    private StatutVoyage statut = StatutVoyage.ACTIF;

    @AssertTrue(message = "Les places disponibles ne peuvent pas dépasser le total des places")
    public boolean isPlacesValid() {
        if (placesDisponibles != null && placesTotal != null) {
            return placesDisponibles <= placesTotal;
        }
        return true;
    }

    @AssertTrue(message = "La date de retour doit être après la date de départ")
    public boolean isDateValid() {
        if (dateDepart != null && dateRetour != null) {
            return dateRetour.isAfter(dateDepart);
        }
        return true;
    }
}