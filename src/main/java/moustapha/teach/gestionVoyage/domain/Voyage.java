package moustapha.teach.gestionVoyage.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Un départ d'autocar entre deux villes, à une date et heure données. */
@Entity
@Table(name = "voyages", indexes = {
        @Index(name = "idx_voyage_trajet_date", columnList = "ville_depart, ville_arrivee, date_depart"),
        @Index(name = "idx_voyage_date", columnList = "date_depart")
})
@Getter
@Setter
@NoArgsConstructor
public class Voyage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ville_depart", nullable = false, length = 60)
    private String villeDepart;

    @Column(name = "ville_arrivee", nullable = false, length = 60)
    private String villeArrivee;

    @Column(name = "gare_depart", length = 120)
    private String gareDepart;

    @Column(name = "gare_arrivee", length = 120)
    private String gareArrivee;

    @Column(name = "date_depart", nullable = false)
    private LocalDateTime dateDepart;

    @Column(name = "duree_minutes", nullable = false)
    private int dureeMinutes;

    /** Prix d'une place en francs CFA (XOF). */
    @Column(nullable = false)
    private int prix;

    @Column(name = "places_total", nullable = false)
    private int placesTotal;

    @Column(name = "places_disponibles", nullable = false)
    private int placesDisponibles;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClasseVoyage classe = ClasseVoyage.STANDARD;

    /** Immatriculation du car, ex. DK-4521-BB. */
    @Column(length = 20)
    private String bus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutVoyage statut = StatutVoyage.PROGRAMME;

    /** Verrou optimiste : empêche la survente de places en cas de réservations simultanées. */
    @Version
    private Long version;

    public LocalDateTime getDateArriveeEstimee() {
        return dateDepart.plusMinutes(dureeMinutes);
    }

    public boolean estReservable() {
        return (statut == StatutVoyage.PROGRAMME || statut == StatutVoyage.EMBARQUEMENT)
                && dateDepart.isAfter(LocalDateTime.now());
    }
}
