package moustapha.teach.gestionVoyage.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Une réservation = un voyage + la liste des passagers (une place chacun, 6 max). */
public record ReservationRequest(@NotNull Long voyageId,
                                 @NotEmpty @Size(max = 6, message = "6 passagers maximum par réservation")
                                 List<@Valid PassagerRequest> passagers) {
}
