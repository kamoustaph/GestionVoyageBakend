package moustapha.teach.gestionVoyage.domain;

/** Rôles de l'application. Un utilisateur a exactement un rôle. */
public enum Role {
    /** Client qui recherche, réserve et paie ses voyages. */
    VOYAGEUR,
    /** Contrôleur en gare : scanne les billets à l'embarquement. */
    AGENT,
    /** Gestionnaire : voyages, utilisateurs, statistiques. */
    ADMIN
}
