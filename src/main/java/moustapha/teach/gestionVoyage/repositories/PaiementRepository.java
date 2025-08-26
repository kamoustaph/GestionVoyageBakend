package moustapha.teach.gestionVoyage.repositories;

import moustapha.teach.gestionVoyage.entities.Paiement;
import moustapha.teach.gestionVoyage.entities.Reservation;
import moustapha.teach.gestionVoyage.enums.MethodePaiement;
import moustapha.teach.gestionVoyage.enums.StatutPaiement;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    Optional<Paiement> findByReservation(Reservation reservation);

    Optional<Paiement> findByReservationId(Long reservationId);

    List<Paiement> findByStatut(StatutPaiement statut);

    List<Paiement> findByMethode(MethodePaiement methode);

    Page<Paiement> findByStatut(StatutPaiement statut, Pageable pageable);
    Page<Paiement> findByMethode(MethodePaiement methode, Pageable pageable);
    Page<Paiement> findByReservationId(Long reservationId, Pageable pageable);

    @Query("SELECT p FROM Paiement p WHERE p.datePaiement BETWEEN :startDate AND :endDate")
    List<Paiement> findByDatePaiementBetween(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Paiement p WHERE p.datePaiement BETWEEN :startDate AND :endDate")
    Page<Paiement> findByDatePaiementBetween(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate,
                                             Pageable pageable);

    @Query("SELECT p FROM Paiement p JOIN p.reservation r WHERE r.user.id = :userId")
    List<Paiement> findByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Paiement p JOIN p.reservation r WHERE r.user.id = :userId")
    Page<Paiement> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT SUM(p.montant) FROM Paiement p WHERE p.statut = 'VALIDE' " +
            "AND p.datePaiement BETWEEN :startDate AND :endDate")
    BigDecimal getTotalValidPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Paiement p WHERE p.montant BETWEEN :minAmount AND :maxAmount")
    List<Paiement> findByMontantRange(@Param("minAmount") BigDecimal minAmount,
                                      @Param("maxAmount") BigDecimal maxAmount);

    @Query("SELECT p FROM Paiement p WHERE p.montant BETWEEN :minAmount AND :maxAmount")
    Page<Paiement> findByMontantRange(@Param("minAmount") BigDecimal minAmount,
                                      @Param("maxAmount") BigDecimal maxAmount,
                                      Pageable pageable);

    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.statut = :statut " +
            "AND p.datePaiement BETWEEN :startDate AND :endDate")
    Long countPaymentsByStatusAndDateRange(@Param("statut") StatutPaiement statut,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Paiement p JOIN p.reservation r WHERE " +
            "(:reservationId IS NULL OR p.reservation.id = :reservationId) AND " +
            "(:userId IS NULL OR r.user.id = :userId) AND " +
            "(:statut IS NULL OR p.statut = :statut) AND " +
            "(:methode IS NULL OR p.methode = :methode) AND " +
            "(:montantMin IS NULL OR p.montant >= :montantMin) AND " +
            "(:montantMax IS NULL OR p.montant <= :montantMax) AND " +
            "(:datePaiementStart IS NULL OR p.datePaiement >= :datePaiementStart) AND " +
            "(:datePaiementEnd IS NULL OR p.datePaiement <= :datePaiementEnd)")
    Page<Paiement> findPaiementsWithFilters(
            @Param("reservationId") Long reservationId,
            @Param("userId") Long userId,
            @Param("statut") StatutPaiement statut,
            @Param("methode") MethodePaiement methode,
            @Param("montantMin") BigDecimal montantMin,
            @Param("montantMax") BigDecimal montantMax,
            @Param("datePaiementStart") LocalDateTime datePaiementStart,
            @Param("datePaiementEnd") LocalDateTime datePaiementEnd,
            Pageable pageable);

    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.statut = :statut")
    Long countByStatut(@Param("statut") StatutPaiement statut);

    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.methode = :methode")
    Long countByMethode(@Param("methode") MethodePaiement methode);

    @Query("SELECT COUNT(p) FROM Paiement p JOIN p.reservation r WHERE r.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p WHERE p.statut = 'VALIDE'")
    BigDecimal getTotalRevenue();


    @Query("SELECT p FROM Paiement p LEFT JOIN FETCH p.reservation r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.voyage WHERE p.id = :id")
    Optional<Paiement> findWithDetailsById(@Param("id") Long id);
}