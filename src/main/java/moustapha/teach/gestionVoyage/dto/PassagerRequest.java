package moustapha.teach.gestionVoyage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PassagerRequest(@NotBlank @Size(min = 2, max = 120) String nom) {
}
