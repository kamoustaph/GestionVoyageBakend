package moustapha.teach.gestionVoyage.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "utilisateurs")
@Getter
@Setter
@NoArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(name = "nom_complet", nullable = false, length = 120)
    private String nomComplet;

    @Column(length = 20)
    private String telephone;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.VOYAGEUR;

    @Column(nullable = false)
    private boolean actif = true;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    public Utilisateur(String email, String nomComplet, String telephone, String motDePasse, Role role) {
        this.email = email;
        this.nomComplet = nomComplet;
        this.telephone = telephone;
        this.motDePasse = motDePasse;
        this.role = role;
    }
}
