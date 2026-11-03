package moustapha.teach.gestionVoyage.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "paiements")
@Getter
@Setter
@NoArgsConstructor
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", unique = true)
    private Reservation reservation;

    @Column(nullable = false)
    private int montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MethodePaiement methode;

    /** Numéro mobile money utilisé (Wave, Orange Money, Free Money). */
    @Column(length = 20)
    private String telephone;

    /** Référence de transaction renvoyée par l'opérateur (simulée ici). */
    @Column(name = "transaction_ref", nullable = false, length = 40)
    private String transactionRef;

    @CreationTimestamp
    @Column(name = "paye_le", updatable = false)
    private LocalDateTime payeLe;
}
