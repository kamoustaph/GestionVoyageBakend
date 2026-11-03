package moustapha.teach.gestionVoyage.repository;

import moustapha.teach.gestionVoyage.domain.StatutVoyage;
import moustapha.teach.gestionVoyage.domain.Voyage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface VoyageRepository extends JpaRepository<Voyage, Long> {

    @Query("""
            select v from Voyage v
            where (:depart is null or lower(v.villeDepart) = lower(:depart))
              and (:arrivee is null or lower(v.villeArrivee) = lower(:arrivee))
              and v.dateDepart >= :debut and v.dateDepart < :fin
              and v.statut in :statuts
            order by v.dateDepart asc
            """)
    List<Voyage> rechercher(@Param("depart") String depart,
                            @Param("arrivee") String arrivee,
                            @Param("debut") LocalDateTime debut,
                            @Param("fin") LocalDateTime fin,
                            @Param("statuts") Collection<StatutVoyage> statuts);

    List<Voyage> findByDateDepartAfterAndStatutInOrderByDateDepartAsc(
            LocalDateTime apres, Collection<StatutVoyage> statuts, Pageable pageable);

    /** Départs proches, pour l'écran de sélection de l'agent. */
    List<Voyage> findByDateDepartBetweenOrderByDateDepartAsc(LocalDateTime debut, LocalDateTime fin);

    List<Voyage> findAllByOrderByDateDepartDesc();

    long countByDateDepartAfterAndStatutNot(LocalDateTime apres, StatutVoyage statut);
}
