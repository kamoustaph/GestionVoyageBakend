package moustapha.teach.gestionVoyage.domain;

/** Résultat d'un scan de QR code par un agent. */
public enum ResultatControle {
    ACCEPTE(true, "Billet valide"),
    DEJA_UTILISE(false, "Billet déjà utilisé"),
    BILLET_ANNULE(false, "Billet annulé"),
    NON_PAYE(false, "Billet non payé"),
    VOYAGE_ANNULE(false, "Voyage annulé"),
    MAUVAIS_VOYAGE(false, "Billet pour un autre départ"),
    HORS_DELAI(false, "Billet hors de sa période de validité"),
    SIGNATURE_INVALIDE(false, "QR code falsifié ou altéré"),
    INCONNU(false, "Billet introuvable"),
    FORMAT_INVALIDE(false, "Ce QR code n'est pas un billet Yoon");

    private final boolean accepte;
    private final String libelle;

    ResultatControle(boolean accepte, String libelle) {
        this.accepte = accepte;
        this.libelle = libelle;
    }

    public boolean isAccepte() { return accepte; }
    public String getLibelle() { return libelle; }
}
