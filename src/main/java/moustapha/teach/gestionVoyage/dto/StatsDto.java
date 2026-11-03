package moustapha.teach.gestionVoyage.dto;

import java.util.List;

public record StatsDto(long chiffreAffaires, long billetsVendus, long billetsEmbarques, long voyagesAVenir,
                       long voyageurs, long agents, long controlesAujourdhui, long refusAujourdhui,
                       int tauxRemplissage, List<VenteJour> ventes14Jours, List<Trajet> topTrajets) {

    public record VenteJour(String jour, long montant, long billets) {
    }

    public record Trajet(String depart, String arrivee, long billets, long montant) {
    }
}
