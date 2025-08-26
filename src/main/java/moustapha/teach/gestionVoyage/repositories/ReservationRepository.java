package moustapha.teach.gestionVoyage.repositories;

import moustapha.teach.gestionVoyage.entities.Reservation;
import moustapha.teach.gestionVoyage.entities.User;
import moustapha.teach.gestionVoyage.entities.Voyage;
import moustapha.teach.gestionVoyage.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Méthodes existantes
    List<Reservation> findByUser(User user);
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByVoyage(Voyage voyage);
    List<Reservation> findByVoyageId(Long voyageId);
    List<Reservation> findByStatut(ReservationStatus statut);

    // Nouvelles méthodes avec pagination
    Page<Reservation> findByUser(User user, Pageable pageable);
    Page<Reservation> findByUserId(Long userId, Pageable pageable);
    Page<Reservation> findByVoyage(Voyage voyage, Pageable pageable);
    Page<Reservation> findByVoyageId(Long voyageId, Pageable pageable);
    Page<Reservation> findByStatut(ReservationStatus statut, Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.statut = :statut")
    List<Reservation> findByUserIdAndStatut(@Param("userId") Long userId,
                                            @Param("statut") ReservationStatus statut);

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.statut = :statut")
    Page<Reservation> findByUserIdAndStatut(@Param("userId") Long userId,
                                            @Param("statut") ReservationStatus statut,
                                            Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.voyage.id = :voyageId AND r.statut = :statut")
    List<Reservation> findByVoyageIdAndStatut(@Param("voyageId") Long voyageId,
                                              @Param("statut") ReservationStatus statut);

    @Query("SELECT r FROM Reservation r WHERE r.voyage.id = :voyageId AND r.statut = :statut")
    Page<Reservation> findByVoyageIdAndStatut(@Param("voyageId") Long voyageId,
                                              @Param("statut") ReservationStatus statut,
                                              Pageable pageable);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.voyage.id = :voyageId AND r.statut = 'CONFIRMED'")
    Long countConfirmedReservationsByVoyageId(@Param("voyageId") Long voyageId);

    Optional<Reservation> findByUserAndVoyageAndStatut(User user, Voyage voyage, ReservationStatus statut);

    @Query("SELECT r FROM Reservation r WHERE r.dateExpiration < :currentDate AND r.statut = 'PENDING'")
    List<Reservation> findExpiredReservations(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT r FROM Reservation r WHERE r.dateExpiration < :currentDate AND r.statut = 'PENDING'")
    Page<Reservation> findExpiredReservations(@Param("currentDate") LocalDateTime currentDate,
                                              Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.dateReservation BETWEEN :startDate AND :endDate")
    List<Reservation> findReservationsByDateRange(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r FROM Reservation r WHERE r.dateReservation BETWEEN :startDate AND :endDate")
    Page<Reservation> findReservationsByDateRange(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate,
                                                  Pageable pageable);

    // Nouvelle méthode pour les filtres avancés avec pagination
    @Query("SELECT r FROM Reservation r WHERE " +
            "(:userId IS NULL OR r.user.id = :userId) AND " +
            "(:voyageId IS NULL OR r.voyage.id = :voyageId) AND " +
            "(:statut IS NULL OR r.statut = :statut) AND " +
            "(:dateReservationStart IS NULL OR r.dateReservation >= :dateReservationStart) AND " +
            "(:dateReservationEnd IS NULL OR r.dateReservation <= :dateReservationEnd) AND " +
            "(:dateExpirationStart IS NULL OR r.dateExpiration >= :dateExpirationStart) AND " +
            "(:dateExpirationEnd IS NULL OR r.dateExpiration <= :dateExpirationEnd) AND " +
            "(:nombrePlacesMin IS NULL OR r.nombrePlaces >= :nombrePlacesMin) AND " +
            "(:nombrePlacesMax IS NULL OR r.nombrePlaces <= :nombrePlacesMax)")
    Page<Reservation> findReservationsWithFilters(
            @Param("userId") Long userId,
            @Param("voyageId") Long voyageId,
            @Param("statut") ReservationStatus statut,
            @Param("dateReservationStart") LocalDateTime dateReservationStart,
            @Param("dateReservationEnd") LocalDateTime dateReservationEnd,
            @Param("dateExpirationStart") LocalDateTime dateExpirationStart,
            @Param("dateExpirationEnd") LocalDateTime dateExpirationEnd,
            @Param("nombrePlacesMin") Integer nombrePlacesMin,
            @Param("nombrePlacesMax") Integer nombrePlacesMax,
            Pageable pageable);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.statut = :statut")
    Long countByStatut(@Param("statut") ReservationStatus statut);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.voyage.id = :voyageId")
    Long countByVoyageId(@Param("voyageId") Long voyageId);


    @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.voyage WHERE r.id = :id")
    Optional<Reservation> findWithDetailsById(@Param("id") Long id);
}