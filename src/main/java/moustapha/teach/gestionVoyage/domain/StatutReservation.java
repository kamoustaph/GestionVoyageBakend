package moustapha.teach.gestionVoyage.domain;

public enum StatutReservation {
    /** Places bloquées, en attente de paiement (15 min par défaut). */
    EN_ATTENTE_PAIEMENT,
    /** Payée : les billets sont émis. */
    CONFIRMEE,
    /** Annulée par le voyageur ou suite à l'annulation du voyage. */
    ANNULEE,
    /** Non payée dans le délai : places libérées. */
    EXPIREE
}
