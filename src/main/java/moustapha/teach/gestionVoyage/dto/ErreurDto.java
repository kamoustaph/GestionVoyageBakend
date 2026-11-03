package moustapha.teach.gestionVoyage.dto;

import java.time.LocalDateTime;
import java.util.Map;

/** Format unique des erreurs renvoyées par l'API. */
public record ErreurDto(int status, String message, Map<String, String> champs, LocalDateTime date) {
}
