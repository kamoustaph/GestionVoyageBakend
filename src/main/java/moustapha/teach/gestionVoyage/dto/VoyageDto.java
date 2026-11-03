package moustapha.teach.gestionVoyage.dto;

import moustapha.teach.gestionVoyage.domain.ClasseVoyage;
import moustapha.teach.gestionVoyage.domain.StatutVoyage;
import moustapha.teach.gestionVoyage.domain.Voyage;

import java.time.LocalDateTime;

public record VoyageDto(Long id, String villeDepart, String villeArrivee, String gareDepart, String gareArrivee,
                        LocalDateTime dateDepart, LocalDateTime dateArriveeEstimee, int dureeMinutes,
                        int prix, int placesTotal, int placesDisponibles, ClasseVoyage classe, String bus,
                        StatutVoyage statut, boolean reservable) {
    public static VoyageDto of(Voyage v) {
        return new VoyageDto(v.getId(), v.getVilleDepart(), v.getVilleArrivee(), v.getGareDepart(), v.getGareArrivee(),
                v.getDateDepart(), v.getDateArriveeEstimee(), v.getDureeMinutes(), v.getPrix(), v.getPlacesTotal(),
                v.getPlacesDisponibles(), v.getClasse(), v.getBus(), v.getStatut(),
                v.estReservable() && v.getPlacesDisponibles() > 0);
    }
}
