package moustapha.teach.gestionVoyage.domain;

public enum StatutBillet {
    /** Réservation pas encore payée : le billet n'est pas utilisable. */
    EN_ATTENTE,
    /** Billet utilisable pour embarquer. */
    VALIDE,
    /** Déjà scanné et embarqué : un second scan est refusé. */
    UTILISE,
    /** Annulé (réservation ou voyage annulé). */
    ANNULE
}
