package moustapha.teach.gestionVoyage.dto;

import jakarta.validation.constraints.*;
import moustapha.teach.gestionVoyage.domain.ClasseVoyage;

import java.time.LocalDateTime;

public record VoyageRequest(
        @NotBlank String villeDepart,
        @NotBlank String villeArrivee,
        String gareDepart,
        String gareArrivee,
        @NotNull @Future LocalDateTime dateDepart,
        @Min(15) @Max(1440) int dureeMinutes,
        @Min(500) @Max(200000) int prix,
        @Min(1) @Max(80) int placesTotal,
        @NotNull ClasseVoyage classe,
        @Size(max = 20) String bus) {
}
