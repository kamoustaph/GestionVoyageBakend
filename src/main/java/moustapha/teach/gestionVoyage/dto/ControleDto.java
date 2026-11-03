package moustapha.teach.gestionVoyage.dto;

import moustapha.teach.gestionVoyage.domain.ResultatControle;

import java.time.LocalDateTime;

public record ControleDto(Long id, LocalDateTime scanneLe, ResultatControle resultat, String message, String agent,
                          String billetCode, String nomPassager, String trajet) {
}
