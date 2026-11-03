package moustapha.teach.gestionVoyage.domain;

public enum StatutVoyage {
    /** Ouvert à la réservation. */
    PROGRAMME,
    /** Le car est à quai, les agents scannent les billets. */
    EMBARQUEMENT,
    /** Le car est parti : plus de réservation ni d'embarquement. */
    PARTI,
    /** Voyage annulé : les billets sont annulés. */
    ANNULE
}
