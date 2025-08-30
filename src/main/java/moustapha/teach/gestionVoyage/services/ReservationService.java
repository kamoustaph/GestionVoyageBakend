package moustapha.teach.gestionVoyage.services;

import moustapha.teach.gestionVoyage.dto.ReservationDetailDto;
import moustapha.teach.gestionVoyage.dto.ReservationDto;
import moustapha.teach.gestionVoyage.dto.ReservationCreateDto;
import moustapha.teach.gestionVoyage.dto.ReservationFilterDto;
import moustapha.teach.gestionVoyage.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationService {

    List<ReservationDto> getAllReservations();
    Optional<ReservationDto> getReservationById(Long id);
    List<ReservationDto> getReservationsByUserId(Long userId);
    List<ReservationDto> getReservationsByVoyageId(Long voyageId);
    List<ReservationDto> getReservationsByStatut(ReservationStatus statut);
    List<ReservationDto> getReservationsByUserIdAndStatut(Long userId, ReservationStatus statut);
    List<ReservationDto> getReservationsByVoyageIdAndStatut(Long voyageId, ReservationStatus statut);
    ReservationDto createReservation(ReservationCreateDto reservationCreateDto);
    Optional<ReservationDto> updateReservationStatut(Long id, ReservationStatus nouveauStatut);
    boolean deleteReservation(Long id);
    boolean confirmReservation(Long id);
    boolean cancelReservation(Long id);
    Long countConfirmedReservationsByVoyageId(Long voyageId);
    List<ReservationDto> getExpiredReservations();
    void processExpiredReservations();
    Optional<ReservationDto> setExpirationDate(Long reservationId, LocalDateTime dateExpiration);
    List<ReservationDto> getReservationsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Page<ReservationDto> getAllReservations(Pageable pageable);
    Page<ReservationDto> getReservationsByUserId(Long userId, Pageable pageable);
    Page<ReservationDto> getReservationsByVoyageId(Long voyageId, Pageable pageable);
    Page<ReservationDto> getReservationsByStatut(ReservationStatus statut, Pageable pageable);
    Page<ReservationDto> getReservationsByUserIdAndStatut(Long userId, ReservationStatus statut, Pageable pageable);
    Page<ReservationDto> getReservationsByVoyageIdAndStatut(Long voyageId, ReservationStatus statut, Pageable pageable);
    Page<ReservationDto> getExpiredReservations(Pageable pageable);
    Page<ReservationDto> getReservationsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<ReservationDto> searchReservationsWithFilters(ReservationFilterDto filterDto, Pageable pageable);

    Long getTotalReservations();
    Long getReservationsByStatutCount(ReservationStatus statut);
    Long getReservationsByUserIdCount(Long userId);
    Long getReservationsByVoyageIdCount(Long voyageId);

    Optional<ReservationDetailDto> getReservationDetail(Long id);

    Page<ReservationDto> searchGlobal(String query, Pageable pageable);
}