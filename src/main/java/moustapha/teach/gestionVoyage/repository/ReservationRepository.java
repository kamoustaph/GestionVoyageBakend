package moustapha.teach.gestionVoyage.repository;

import moustapha.teach.gestionVoyage.domain.Reservation;
import moustapha.teach.gestionVoyage.domain.StatutReservation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @EntityGraph(attributePaths = {"voyage", "billets", "paiement"})
    List<Reservation> findByUtilisateurIdOrderByCreeLeDesc(Long utilisateurId);

    @EntityGraph(attributePaths = {"voyage", "utilisateur", "paiement"})
    List<Reservation> findTop200ByOrderByCreeLeDesc();

    @EntityGraph(attributePaths = {"voyage", "billets", "paiement", "utilisateur"})
    Optional<Reservation> findWithDetailsById(Long id);

    boolean existsByReference(String reference);

    List<Reservation> findByStatutAndExpireLeBefore(StatutReservation statut, LocalDateTime limite);

    List<Reservation> findByVoyageIdAndStatutIn(Long voyageId, List<StatutReservation> statuts);

    /** Chiffre d'affaires et billets par jour de paiement (statistiques admin). */
    @Query("""
            select cast(p.payeLe as LocalDate), sum(p.montant), sum(r.nombrePlaces)
            from Paiement p join p.reservation r
            where p.payeLe >= :depuis and r.statut = moustapha.teach.gestionVoyage.domain.StatutReservation.CONFIRMEE
            group by cast(p.payeLe as LocalDate)
            """)
    List<Object[]> ventesParJour(LocalDateTime depuis);

    @Query("""
            select coalesce(sum(r.montantTotal), 0) from Reservation r
            where r.statut = moustapha.teach.gestionVoyage.domain.StatutReservation.CONFIRMEE
            """)
    long chiffreAffairesTotal();

    @Query("""
            select v.villeDepart, v.villeArrivee, sum(r.nombrePlaces), sum(r.montantTotal)
            from Reservation r join r.voyage v
            where r.statut = moustapha.teach.gestionVoyage.domain.StatutReservation.CONFIRMEE
            group by v.villeDepart, v.villeArrivee
            order by sum(r.nombrePlaces) desc
            """)
    List<Object[]> topTrajets();
}
