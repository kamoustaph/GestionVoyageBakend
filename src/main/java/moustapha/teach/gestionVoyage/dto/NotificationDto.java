package moustapha.teach.gestionVoyage.dto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;

    @NotNull(message = "Le titre est obligatoire")
    private String title;

    @NotNull(message = "Le message est obligatoire")
    private String message;

}