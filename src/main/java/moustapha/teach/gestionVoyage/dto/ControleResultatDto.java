package moustapha.teach.gestionVoyage.dto;

import moustapha.teach.gestionVoyage.domain.ResultatControle;

/** Réponse au scan : verdict + détails du billet pour vérifier l'identité du passager. */
public record ControleResultatDto(ResultatControle resultat, boolean accepte, String message, BilletDto billet) {
}
