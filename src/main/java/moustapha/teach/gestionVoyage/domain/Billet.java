package moustapha.teach.gestionVoyage.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/** Un billet = une place pour un passager. Il porte le QR code présenté à l'embarquement. */
@Entity
@Table(name = "billets", indexes = @Index(name = "idx_billet_voyage", columnList = "voyage_id"))
@Getter
@Setter
@NoArgsConstructor
public class Billet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Code unique et non devinable du billet, ex. 8HKT3QZP2W. */
    @Column(nullable = false, unique = true, length = 16)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voyage_id")
    private Voyage voyage;

    @Column(name = "nom_passager", nullable = false, length = 120)
    private String nomPassager;

    @Column(nullable = false)
    private int siege;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutBillet statut = StatutBillet.VALIDE;

    @CreationTimestamp
    @Column(name = "emis_le", updatable = false)
    private LocalDateTime emisLe;

    @Column(name = "embarque_le")
    private LocalDateTime embarqueLe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "embarque_par_id")
    private Utilisateur embarquePar;
}
