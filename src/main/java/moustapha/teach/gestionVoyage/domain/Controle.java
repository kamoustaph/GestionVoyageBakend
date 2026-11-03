package moustapha.teach.gestionVoyage.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/** Journal d'audit : chaque scan (accepté ou refusé) fait par un agent. */
@Entity
@Table(name = "controles", indexes = @Index(name = "idx_controle_date", columnList = "scanne_le"))
@Getter
@Setter
@NoArgsConstructor
public class Controle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agent_id")
    private Utilisateur agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billet_id")
    private Billet billet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ResultatControle resultat;

    /** Début du contenu scanné, utile pour analyser les tentatives de fraude. */
    @Column(name = "contenu_scanne", length = 120)
    private String contenuScanne;

    @CreationTimestamp
    @Column(name = "scanne_le", updatable = false)
    private LocalDateTime scanneLe;
}
