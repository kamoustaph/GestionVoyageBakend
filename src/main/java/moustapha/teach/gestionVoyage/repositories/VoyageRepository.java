package moustapha.teach.gestionVoyage.repositories;

import moustapha.teach.gestionVoyage.entities.Voyage;
import moustapha.teach.gestionVoyage.enums.StatutVoyage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VoyageRepository extends JpaRepository<Voyage, Long> {

    // Méthodes existantes sans pagination
    List<Voyage> findByStatut(StatutVoyage statut);
    List<Voyage> findByLieuDepartContainingIgnoreCase(String lieuDepart);
    List<Voyage> findByLieuArriveeContainingIgnoreCase(String lieuArrivee);

    // Nouvelles méthodes avec pagination
    Page<Voyage> findByStatut(StatutVoyage statut, Pageable pageable);
    Page<Voyage> findByLieuDepartContainingIgnoreCase(String lieuDepart, Pageable pageable);
    Page<Voyage> findByLieuArriveeContainingIgnoreCase(String lieuArrivee, Pageable pageable);

    // Recherche avec filtres et pagination
    @Query("SELECT v FROM Voyage v WHERE " +
            "(:lieuDepart IS NULL OR LOWER(v.lieuDepart) LIKE LOWER(CONCAT('%', :lieuDepart, '%'))) AND " +
            "(:lieuArrivee IS NULL OR LOWER(v.lieuArrivee) LIKE LOWER(CONCAT('%', :lieuArrivee, '%'))) AND " +
            "(:statut IS NULL OR v.statut = :statut) AND " +
            "(:prixMin IS NULL OR v.prix >= :prixMin) AND " +
            "(:prixMax IS NULL OR v.prix <= :prixMax) AND " +
            "(:dateDepart IS NULL OR v.dateDepart >= :dateDepart) AND " +
            "(:dateRetour IS NULL OR v.dateRetour <= :dateRetour) AND " +
            "(:placesMin IS NULL OR v.placesDisponibles >= :placesMin)")
    Page<Voyage> findVoyagesWithFilters(
            @Param("lieuDepart") String lieuDepart,
            @Param("lieuArrivee") String lieuArrivee,
            @Param("statut") StatutVoyage statut,
            @Param("prixMin") BigDecimal prixMin,
            @Param("prixMax") BigDecimal prixMax,
            @Param("dateDepart") LocalDateTime dateDepart,
            @Param("dateRetour") LocalDateTime dateRetour,
            @Param("placesMin") Integer placesMin,
            Pageable pageable);

    @Query("SELECT v FROM Voyage v WHERE LOWER(v.lieuDepart) LIKE LOWER(CONCAT('%', :lieuDepart, '%')) " +
            "AND LOWER(v.lieuArrivee) LIKE LOWER(CONCAT('%', :lieuArrivee, '%'))")
    List<Voyage> findByLieuDepartAndLieuArrivee(@Param("lieuDepart") String lieuDepart,
                                                @Param("lieuArrivee") String lieuArrivee);

    @Query("SELECT v FROM Voyage v WHERE LOWER(v.lieuDepart) LIKE LOWER(CONCAT('%', :lieuDepart, '%')) " +
            "AND LOWER(v.lieuArrivee) LIKE LOWER(CONCAT('%', :lieuArrivee, '%'))")
    Page<Voyage> findByLieuDepartAndLieuArrivee(@Param("lieuDepart") String lieuDepart,
                                                @Param("lieuArrivee") String lieuArrivee,
                                                Pageable pageable);

    @Query("SELECT v FROM Voyage v WHERE v.dateDepart >= :dateDepart AND v.dateRetour <= :dateRetour")
    List<Voyage> findByDateRange(@Param("dateDepart") LocalDateTime dateDepart,
                                 @Param("dateRetour") LocalDateTime dateRetour);

    @Query("SELECT v FROM Voyage v WHERE v.dateDepart >= :dateDepart AND v.dateRetour <= :dateRetour")
    Page<Voyage> findByDateRange(@Param("dateDepart") LocalDateTime dateDepart,
                                 @Param("dateRetour") LocalDateTime dateRetour,
                                 Pageable pageable);

    @Query("SELECT v FROM Voyage v WHERE v.placesDisponibles > 0 AND v.statut = 'ACTIF'")
    List<Voyage> findAvailableVoyages();

    @Query("SELECT v FROM Voyage v WHERE v.placesDisponibles > 0 AND v.statut = 'ACTIF'")
    Page<Voyage> findAvailableVoyages(Pageable pageable);

    @Query("SELECT v FROM Voyage v WHERE LOWER(v.lieuDepart) LIKE LOWER(CONCAT('%', :lieuDepart, '%')) " +
            "AND LOWER(v.lieuArrivee) LIKE LOWER(CONCAT('%', :lieuArrivee, '%')) " +
            "AND v.dateDepart >= :dateDepart AND v.placesDisponibles > 0 AND v.statut = 'ACTIF'")
    List<Voyage> findAvailableVoyagesByRoute(@Param("lieuDepart") String lieuDepart,
                                             @Param("lieuArrivee") String lieuArrivee,
                                             @Param("dateDepart") LocalDateTime dateDepart);

    @Query("SELECT v FROM Voyage v WHERE LOWER(v.lieuDepart) LIKE LOWER(CONCAT('%', :lieuDepart, '%')) " +
            "AND LOWER(v.lieuArrivee) LIKE LOWER(CONCAT('%', :lieuArrivee, '%')) " +
            "AND v.dateDepart >= :dateDepart AND v.placesDisponibles > 0 AND v.statut = 'ACTIF'")
    Page<Voyage> findAvailableVoyagesByRoute(@Param("lieuDepart") String lieuDepart,
                                             @Param("lieuArrivee") String lieuArrivee,
                                             @Param("dateDepart") LocalDateTime dateDepart,
                                             Pageable pageable);

    @Query("SELECT v FROM Voyage v WHERE v.dateDepart >= :startDate AND v.dateDepart <= :endDate")
    List<Voyage> findVoyagesByDateDepartBetween(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT v FROM Voyage v WHERE v.dateDepart >= :startDate AND v.dateDepart <= :endDate")
    Page<Voyage> findVoyagesByDateDepartBetween(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate,
                                                Pageable pageable);

    Page<Voyage> findByPrixBetween(BigDecimal prixMin, BigDecimal prixMax, Pageable pageable);

    @Query("SELECT COUNT(v) FROM Voyage v WHERE v.statut = :statut")
    Long countByStatut(@Param("statut") StatutVoyage statut);

    @Query("SELECT COUNT(v) FROM Voyage v WHERE v.placesDisponibles > 0 AND v.statut = 'ACTIF'")
    Long countAvailableVoyages();
}