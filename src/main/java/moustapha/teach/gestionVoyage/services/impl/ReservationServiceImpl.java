package moustapha.teach.gestionVoyage.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moustapha.teach.gestionVoyage.dto.ReservationDetailDto;
import moustapha.teach.gestionVoyage.dto.ReservationDto;
import moustapha.teach.gestionVoyage.dto.ReservationCreateDto;
import moustapha.teach.gestionVoyage.dto.ReservationFilterDto;
import moustapha.teach.gestionVoyage.entities.Reservation;
import moustapha.teach.gestionVoyage.entities.User;
import moustapha.teach.gestionVoyage.entities.Voyage;
import moustapha.teach.gestionVoyage.mapper.ReservationMapper;
import moustapha.teach.gestionVoyage.repositories.ReservationRepository;
import moustapha.teach.gestionVoyage.repositories.UserRepository;
import moustapha.teach.gestionVoyage.repositories.VoyageRepository;
import moustapha.teach.gestionVoyage.enums.ReservationStatus;
import moustapha.teach.gestionVoyage.services.ReservationService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final VoyageRepository voyageRepository;
    private final ReservationMapper reservationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReservationDto> getReservationById(Long id) {
        return reservationRepository.findById(id)
                .map(reservationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByVoyageId(Long voyageId) {
        return reservationRepository.findByVoyageId(voyageId)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByStatut(ReservationStatus statut) {
        return reservationRepository.findByStatut(statut)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByUserIdAndStatut(Long userId, ReservationStatus statut) {
        return reservationRepository.findByUserIdAndStatut(userId, statut)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByVoyageIdAndStatut(Long voyageId, ReservationStatus statut) {
        return reservationRepository.findByVoyageIdAndStatut(voyageId, statut)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationDto createReservation(ReservationCreateDto reservationCreateDto) {
        log.info("Création d'une nouvelle réservation pour l'utilisateur {} et le voyage {} avec {} places",
                reservationCreateDto.getUserId(), reservationCreateDto.getVoyageId(), reservationCreateDto.getNombrePlaces());

        User user = userRepository.findById(reservationCreateDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Voyage voyage = voyageRepository.findById(reservationCreateDto.getVoyageId())
                .orElseThrow(() -> new RuntimeException("Voyage non trouvé"));

        // Vérifier la disponibilité
        if (voyage.getPlacesDisponibles() < reservationCreateDto.getNombrePlaces()) {
            throw new RuntimeException("Places insuffisantes pour ce voyage. Disponible: " + voyage.getPlacesDisponibles() + ", Demandé: " + reservationCreateDto.getNombrePlaces());
        }

        // Vérifier qu'il n'existe pas déjà une réservation active pour ce user/voyage
        Optional<Reservation> existingReservation = reservationRepository.findByUserAndVoyageAndStatut(
                user, voyage, ReservationStatus.CONFIRMED);
        if (existingReservation.isPresent()) {
            throw new RuntimeException("Une réservation confirmée existe déjà pour ce voyage");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setVoyage(voyage);
        reservation.setStatut(ReservationStatus.PENDING);
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setDateExpiration(LocalDateTime.now().plusHours(24));
        reservation.setNombrePlaces(reservationCreateDto.getNombrePlaces()); // ← CORRECTION ICI

        // Décrémenter les places disponibles
        voyage.setPlacesDisponibles(voyage.getPlacesDisponibles() - reservationCreateDto.getNombrePlaces()); // ← CORRECTION ICI
        voyageRepository.save(voyage);

        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Réservation créée avec l'ID {} pour {} places", savedReservation.getId(), savedReservation.getNombrePlaces());

        return reservationMapper.toDto(savedReservation);
    }

    @Override
    public Optional<ReservationDto> updateReservationStatut(Long id, ReservationStatus nouveauStatut) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    ReservationStatus ancienStatut = reservation.getStatut();
                    reservation.setStatut(nouveauStatut);

                    // Si on annule une réservation, libérer la place
                    if (nouveauStatut == ReservationStatus.CANCELLED &&
                            (ancienStatut == ReservationStatus.PENDING || ancienStatut == ReservationStatus.CONFIRMED)) {
                        Voyage voyage = reservation.getVoyage();
                        voyage.setPlacesDisponibles(voyage.getPlacesDisponibles() + reservation.getNombrePlaces()); // ← CORRECTION ICI
                        voyageRepository.save(voyage);
                    }

                    return reservationMapper.toDto(reservationRepository.save(reservation));
                });
    }

    @Override
    public boolean deleteReservation(Long id) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (reservation.isPresent()) {
            Reservation res = reservation.get();
            // Libérer la place si la réservation était active
            if (res.getStatut() == ReservationStatus.PENDING ||
                    res.getStatut() == ReservationStatus.CONFIRMED) {
                Voyage voyage = res.getVoyage();
                voyage.setPlacesDisponibles(voyage.getPlacesDisponibles() + res.getNombrePlaces()); // ← CORRECTION ICI
                voyageRepository.save(voyage);
            }
            reservationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean confirmReservation(Long id) {
        return updateReservationStatut(id, ReservationStatus.CONFIRMED).isPresent();
    }

    @Override
    public boolean cancelReservation(Long id) {
        return updateReservationStatut(id, ReservationStatus.CANCELLED).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countConfirmedReservationsByVoyageId(Long voyageId) {
        return reservationRepository.countConfirmedReservationsByVoyageId(voyageId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getExpiredReservations() {
        return reservationRepository.findExpiredReservations(LocalDateTime.now())
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void processExpiredReservations() {
        List<Reservation> expiredReservations = reservationRepository.findExpiredReservations(LocalDateTime.now());
        for (Reservation reservation : expiredReservations) {
            reservation.setStatut(ReservationStatus.CANCELLED);
            // Libérer la place
            Voyage voyage = reservation.getVoyage();
            voyage.setPlacesDisponibles(voyage.getPlacesDisponibles() + reservation.getNombrePlaces()); // ← CORRECTION ICI
            voyageRepository.save(voyage);
            reservationRepository.save(reservation);
        }
        log.info("Traité {} réservations expirées", expiredReservations.size());
    }

    @Override
    public Optional<ReservationDto> setExpirationDate(Long reservationId, LocalDateTime dateExpiration) {
        return reservationRepository.findById(reservationId)
                .map(reservation -> {
                    reservation.setDateExpiration(dateExpiration);
                    return reservationMapper.toDto(reservationRepository.save(reservation));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return reservationRepository.findReservationsByDateRange(startDate, endDate)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDto> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable)
                .map(reservationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsByUserId(Long userId, Pageable pageable) {
        return reservationRepository.findByUserId(userId, pageable)
                .map(reservationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsByVoyageId(Long voyageId, Pageable pageable) {
        return reservationRepository.findByVoyageId(voyageId, pageable)
                .map(reservationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsByStatut(ReservationStatus statut, Pageable pageable) {
        return reservationRepository.findByStatut(statut, pageable)
                .map(reservationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsByUserIdAndStatut(Long userId, ReservationStatus statut, Pageable pageable) {
        return reservationRepository.findByUserIdAndStatut(userId, statut, pageable)
                .map(reservationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsByVoyageIdAndStatut(Long voyageId, ReservationStatus statut, Pageable pageable) {
        return reservationRepository.findByVoyageIdAndStatut(voyageId, statut, pageable)
                .map(reservationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDto> getExpiredReservations(Pageable pageable) {
        return reservationRepository.findExpiredReservations(LocalDateTime.now(), pageable)
                .map(reservationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return reservationRepository.findReservationsByDateRange(startDate, endDate, pageable)
                .map(reservationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDto> searchReservationsWithFilters(ReservationFilterDto filterDto, Pageable pageable) {
        return reservationRepository.findReservationsWithFilters(
                filterDto.getUserId(),
                filterDto.getVoyageId(),
                filterDto.getStatut(),
                filterDto.getDateReservationStart(),
                filterDto.getDateReservationEnd(),
                filterDto.getDateExpirationStart(),
                filterDto.getDateExpirationEnd(),
                filterDto.getNombrePlacesMin(),
                filterDto.getNombrePlacesMax(),
                pageable
        ).map(reservationMapper::toDto);
    }

    // Méthodes de statistiques
    @Override
    @Transactional(readOnly = true)
    public Long getTotalReservations() {
        return reservationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getReservationsByStatutCount(ReservationStatus statut) {
        return reservationRepository.countByStatut(statut);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getReservationsByUserIdCount(Long userId) {
        return reservationRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getReservationsByVoyageIdCount(Long voyageId) {
        return reservationRepository.countByVoyageId(voyageId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReservationDetailDto> getReservationDetail(Long id) {
        return reservationRepository.findWithDetailsById(id)
                .map(reservation -> {
                    if (reservation.getUser() != null) {
                        reservation.getUser().getUsername();
                    }
                    if (reservation.getVoyage() != null) {
                        reservation.getVoyage().getLieuDepart();
                    }
                    return reservationMapper.toDetailDto(reservation);
                });
    }

    @Transactional(readOnly = true)
    public Page<ReservationDto> searchGlobal(String query, Pageable pageable) {
        return reservationRepository.findByGlobalSearch(query, pageable)
                .map(reservationMapper::toDto);
    }
}