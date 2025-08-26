package moustapha.teach.gestionVoyage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreateDto {

    @NotNull(message = "La réservation est obligatoire")
    private Long reservationId;
}