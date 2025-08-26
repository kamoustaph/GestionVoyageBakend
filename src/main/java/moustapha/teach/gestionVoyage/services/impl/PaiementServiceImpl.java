package moustapha.teach.gestionVoyage.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moustapha.teach.gestionVoyage.dto.PaiementDto;
import moustapha.teach.gestionVoyage.dto.PaiementCreateDto;
import moustapha.teach.gestionVoyage.entities.Paiement;
import moustapha.teach.gestionVoyage.entities.Reservation;
import moustapha.teach.gestionVoyage.mapper.PaiementMapper;
import moustapha.teach.gestionVoyage.repositories.PaiementRepository;
import moustapha.teach.gestionVoyage.repositories.ReservationRepository;
import moustapha.teach.gestionVoyage.enums.MethodePaiement;
import moustapha.teach.gestionVoyage.enums.StatutPaiement;
import moustapha.teach.gestionVoyage.services.PaiementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import moustapha.teach.gestionVoyage.dto.PaiementDetailDto;
import moustapha.teach.gestionVoyage.dto.PaiementFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaiementServiceImpl implements PaiementService {

    private final PaiementRepository paiementRepository;
    private final ReservationRepository reservationRepository;
    private final PaiementMapper paiementMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PaiementDto> getAllPaiements() {
        return paiementRepository.findAll()
                .stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaiementDto> getPaiementById(Long id) {
        return paiementRepository.findById(id)
                .map(paiementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaiementDto> getPaiementByReservationId(Long reservationId) {
        return paiementRepository.findByReservationId(reservationId)
                .map(paiementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementDto> getPaiementsByStatut(StatutPaiement statut) {
        return paiementRepository.findByStatut(statut)
                .stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementDto> getPaiementsByMethode(MethodePaiement methode) {
        return paiementRepository.findByMethode(methode)
                .stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementDto> getPaiementsByUserId(Long userId) {
        return paiementRepository.findByUserId(userId)
                .stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaiementDto createPaiement(PaiementCreateDto paiementCreateDto) {
        log.info("Création d'un nouveau paiement pour la réservation {}", paiementCreateDto.getReservationId());

        Reservation reservation = reservationRepository.findById(paiementCreateDto.getReservationId())
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        // Vérifier qu'il n'existe pas déjà un paiement pour cette réservation
        if (paiementRepository.findByReservationId(paiementCreateDto.getReservationId()).isPresent()) {
            throw new RuntimeException("Un paiement existe déjà pour cette réservation");
        }

        Paiement paiement = new Paiement();
        paiement.setReservation(reservation);
        paiement.setMontant(paiementCreateDto.getMontant());
        paiement.setMethode(paiementCreateDto.getMethode());
        paiement.setStatut(StatutPaiement.EN_ATTENTE);
        paiement.setDatePaiement(LocalDateTime.now());

        Paiement savedPaiement = paiementRepository.save(paiement);
        log.info("Paiement créé avec l'ID {}", savedPaiement.getId());

        return paiementMapper.toDto(savedPaiement);
    }

    @Override
    public Optional<PaiementDto> updatePaiementStatut(Long id, StatutPaiement nouveauStatut) {
        return paiementRepository.findById(id)
                .map(paiement -> {
                    paiement.setStatut(nouveauStatut);
                    paiement.setUpdatedAt(LocalDateTime.now());
                    return paiementMapper.toDto(paiementRepository.save(paiement));
                });
    }

    @Override
    public boolean deletePaiement(Long id) {
        if (paiementRepository.existsById(id)) {
            paiementRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaiementDto> getAllPaiements(Pageable pageable) {
        return paiementRepository.findAll(pageable)
                .map(paiementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaiementDto> getPaiementsByStatut(StatutPaiement statut, Pageable pageable) {
        return paiementRepository.findByStatut(statut, pageable)
                .map(paiementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaiementDto> getPaiementsByMethode(MethodePaiement methode, Pageable pageable) {
        return paiementRepository.findByMethode(methode, pageable)
                .map(paiementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaiementDto> getPaiementsByReservationId(Long reservationId, Pageable pageable) {
        return paiementRepository.findByReservationId(reservationId, pageable)
                .map(paiementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaiementDto> getPaiementsByUserId(Long userId, Pageable pageable) {
        return paiementRepository.findByUserId(userId, pageable)
                .map(paiementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaiementDto> getPaiementsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return paiementRepository.findByDatePaiementBetween(startDate, endDate, pageable)
                .map(paiementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaiementDto> getPaiementsByMontantRange(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable) {
        return paiementRepository.findByMontantRange(minAmount, maxAmount, pageable)
                .map(paiementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaiementDto> searchPaiementsWithFilters(PaiementFilterDto filterDto, Pageable pageable) {
        return paiementRepository.findPaiementsWithFilters(
                filterDto.getReservationId(),
                filterDto.getUserId(),
                filterDto.getStatut(),
                filterDto.getMethode(),
                filterDto.getMontantMin(),
                filterDto.getMontantMax(),
                filterDto.getDatePaiementStart(),
                filterDto.getDatePaiementEnd(),
                pageable
        ).map(paiementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaiementDetailDto> getPaiementDetail(Long id) {
        return paiementRepository.findWithDetailsById(id)
                .map(paiementMapper::toDetailDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalPaiements() {
        return paiementRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPaiementsByStatutCount(StatutPaiement statut) {
        return paiementRepository.countByStatut(statut);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPaiementsByMethodeCount(MethodePaiement methode) {
        return paiementRepository.countByMethode(methode);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPaiementsByUserIdCount(Long userId) {
        return paiementRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        return paiementRepository.getTotalRevenue();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getRevenueBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return paiementRepository.getTotalValidPaymentsBetweenDates(startDate, endDate);
    }
}
