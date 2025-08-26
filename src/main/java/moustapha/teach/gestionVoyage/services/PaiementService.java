package moustapha.teach.gestionVoyage.services;

import moustapha.teach.gestionVoyage.dto.PaiementCreateDto;
import moustapha.teach.gestionVoyage.dto.PaiementDetailDto;
import moustapha.teach.gestionVoyage.dto.PaiementDto;
import moustapha.teach.gestionVoyage.dto.PaiementFilterDto;
import moustapha.teach.gestionVoyage.enums.MethodePaiement;
import moustapha.teach.gestionVoyage.enums.StatutPaiement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaiementService {

    List<PaiementDto> getAllPaiements();
    Optional<PaiementDto> getPaiementById(Long id);
    Optional<PaiementDto> getPaiementByReservationId(Long reservationId);
    List<PaiementDto> getPaiementsByStatut(StatutPaiement statut);
    List<PaiementDto> getPaiementsByMethode(MethodePaiement methode);
    List<PaiementDto> getPaiementsByUserId(Long userId);
    PaiementDto createPaiement(PaiementCreateDto paiementCreateDto);
    Optional<PaiementDto> updatePaiementStatut(Long id, StatutPaiement nouveauStatut);
    boolean deletePaiement(Long id);

    Page<PaiementDto> getAllPaiements(Pageable pageable);
    Page<PaiementDto> getPaiementsByStatut(StatutPaiement statut, Pageable pageable);
    Page<PaiementDto> getPaiementsByMethode(MethodePaiement methode, Pageable pageable);
    Page<PaiementDto> getPaiementsByReservationId(Long reservationId, Pageable pageable);
    Page<PaiementDto> getPaiementsByUserId(Long userId, Pageable pageable);
    Page<PaiementDto> getPaiementsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<PaiementDto> getPaiementsByMontantRange(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable);

    Page<PaiementDto> searchPaiementsWithFilters(PaiementFilterDto filterDto, Pageable pageable);

    Optional<PaiementDetailDto> getPaiementDetail(Long id);

    Long getTotalPaiements();
    Long getPaiementsByStatutCount(StatutPaiement statut);
    Long getPaiementsByMethodeCount(MethodePaiement methode);
    Long getPaiementsByUserIdCount(Long userId);
    BigDecimal getTotalRevenue();
    BigDecimal getRevenueBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
}