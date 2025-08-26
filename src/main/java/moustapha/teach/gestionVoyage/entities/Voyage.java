package moustapha.teach.gestionVoyage.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moustapha.teach.gestionVoyage.enums.StatutVoyage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "voyages")
public class Voyage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "voyage_seq")
    @SequenceGenerator(name = "voyage_seq", sequenceName = "voyage_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "lieu_depart", nullable = false)
    private String lieuDepart;

    @Column(name = "lieu_arrivee", nullable = false)
    private String lieuArrivee;

    @Column(name = "date_depart", nullable = false)
    private LocalDateTime dateDepart;

    @Column(name = "date_retour", nullable = false)
    private LocalDateTime dateRetour;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    @Column(name = "places_disponibles", nullable = false)
    private Integer placesDisponibles;

    @Column(name = "places_total", nullable = false)
    private Integer placesTotal;

    @Enumerated(EnumType.STRING)
    private StatutVoyage statut = StatutVoyage.ACTIF;

}
