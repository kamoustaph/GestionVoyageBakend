package moustapha.teach.gestionVoyage.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Référence lisible communiquée au client, ex. YN-7KQ2M9. */
    @Column(nullable = false, unique = true, length = 20)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voyage_id")
    private Voyage voyage;

    @Column(name = "nombre_places", nullable = false)
    private int nombrePlaces;

    @Column(name = "montant_total", nullable = false)
    private int montantTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutReservation statut = StatutReservation.EN_ATTENTE_PAIEMENT;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    /** Date limite de paiement ; au-delà la réservation expire et libère les places. */
    @Column(name = "expire_le")
    private LocalDateTime expireLe;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("siege ASC")
    private List<Billet> billets = new ArrayList<>();

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private Paiement paiement;
}
