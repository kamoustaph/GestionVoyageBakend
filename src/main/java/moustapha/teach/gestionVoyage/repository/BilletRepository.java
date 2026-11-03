package moustapha.teach.gestionVoyage.repository;

import moustapha.teach.gestionVoyage.domain.Billet;
import moustapha.teach.gestionVoyage.domain.StatutBillet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BilletRepository extends JpaRepository<Billet, Long> {

    @EntityGraph(attributePaths = {"voyage", "reservation", "reservation.utilisateur", "embarquePar"})
    Optional<Billet> findByCode(String code);

    boolean existsByCode(String code);

    @EntityGraph(attributePaths = {"voyage", "reservation"})
    @Query("select b from Billet b where b.reservation.utilisateur.id = :utilisateurId order by b.voyage.dateDepart desc, b.siege asc")
    List<Billet> findDuVoyageur(Long utilisateurId);

    @EntityGraph(attributePaths = {"voyage", "reservation", "embarquePar"})
    List<Billet> findByVoyageIdAndStatutInOrderBySiegeAsc(Long voyageId, List<StatutBillet> statuts);

    @Query("select coalesce(max(b.siege), 0) from Billet b where b.voyage.id = :voyageId")
    int dernierSiege(Long voyageId);

    long countByStatut(StatutBillet statut);
}
