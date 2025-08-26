package moustapha.teach.gestionVoyage.repositories;

import moustapha.teach.gestionVoyage.entities.Reservation;
import moustapha.teach.gestionVoyage.entities.Ticket;
import moustapha.teach.gestionVoyage.enums.StatutTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByReservation(Reservation reservation);

    Optional<Ticket> findByReservationId(Long reservationId);

    Optional<Ticket> findByQrCode(String qrCode);

    List<Ticket> findByStatut(StatutTicket statut);

    @Query("SELECT t FROM Ticket t JOIN t.reservation r WHERE r.user.id = :userId")
    List<Ticket> findByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Ticket t JOIN t.reservation r WHERE r.voyage.id = :voyageId")
    List<Ticket> findByVoyageId(@Param("voyageId") Long voyageId);

    Page<Ticket> findByStatut(StatutTicket statut, Pageable pageable);
    Page<Ticket> findByReservationId(Long reservationId, Pageable pageable);
    Page<Ticket> findByQrCodeContaining(String qrCode, Pageable pageable);



    @Query("SELECT t FROM Ticket t JOIN t.reservation r WHERE r.user.id = :userId")
    Page<Ticket> findByUserId(@Param("userId") Long userId, Pageable pageable);



    @Query("SELECT t FROM Ticket t JOIN t.reservation r WHERE r.voyage.id = :voyageId")
    Page<Ticket> findByVoyageId(@Param("voyageId") Long voyageId, Pageable pageable);

    boolean existsByQrCode(String qrCode);

    @Query("SELECT t FROM Ticket t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Ticket> findTicketsByDateRange(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Ticket t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    Page<Ticket> findTicketsByDateRange(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        Pageable pageable);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.statut = :statut")
    Long countTicketsByStatus(@Param("statut") StatutTicket statut);

    @Query("SELECT t FROM Ticket t JOIN t.reservation r JOIN r.voyage v " +
            "WHERE v.dateDepart < :currentDate AND t.statut = 'VALIDE'")
    List<Ticket> findTicketsToExpire(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT t FROM Ticket t JOIN t.reservation r JOIN r.voyage v JOIN r.user u WHERE " +
            "(:reservationId IS NULL OR t.reservation.id = :reservationId) AND " +
            "(:userId IS NULL OR r.user.id = :userId) AND " +
            "(:voyageId IS NULL OR r.voyage.id = :voyageId) AND " +
            "(:statut IS NULL OR t.statut = :statut) AND " +
            "(:qrCode IS NULL OR t.qrCode LIKE %:qrCode%) AND " +
            "(:createdAtStart IS NULL OR t.createdAt >= :createdAtStart) AND " +
            "(:createdAtEnd IS NULL OR t.createdAt <= :createdAtEnd)")
    Page<Ticket> findTicketsWithFilters(
            @Param("reservationId") Long reservationId,
            @Param("userId") Long userId,
            @Param("voyageId") Long voyageId,
            @Param("statut") StatutTicket statut,
            @Param("qrCode") String qrCode,
            @Param("createdAtStart") LocalDateTime createdAtStart,
            @Param("createdAtEnd") LocalDateTime createdAtEnd,
            Pageable pageable);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.statut = :statut")
    Long countByStatut(@Param("statut") StatutTicket statut);

    @Query("SELECT COUNT(t) FROM Ticket t JOIN t.reservation r WHERE r.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Ticket t JOIN t.reservation r WHERE r.voyage.id = :voyageId")
    Long countByVoyageId(@Param("voyageId") Long voyageId);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.reservation r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.voyage WHERE t.id = :id")
    Optional<Ticket> findWithDetailsById(@Param("id") Long id);
    Optional<Ticket> findById(Long id);

}