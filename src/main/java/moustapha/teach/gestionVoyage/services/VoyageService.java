package moustapha.teach.gestionVoyage.services;

import moustapha.teach.gestionVoyage.dto.VoyageDto;
import moustapha.teach.gestionVoyage.dto.VoyageCreateDto;
import moustapha.teach.gestionVoyage.dto.VoyageFilterDto;
import moustapha.teach.gestionVoyage.enums.StatutVoyage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VoyageService {

    List<VoyageDto> getAllVoyages();
    Optional<VoyageDto> getVoyageById(Long id);
    List<VoyageDto> getVoyagesByStatut(StatutVoyage statut);
    List<VoyageDto> searchVoyagesByLieuDepart(String lieuDepart);
    List<VoyageDto> searchVoyagesByLieuArrivee(String lieuArrivee);
    List<VoyageDto> searchVoyagesByRoute(String lieuDepart, String lieuArrivee);
    List<VoyageDto> getVoyagesByDateRange(LocalDateTime dateDepart, LocalDateTime dateRetour);
    List<VoyageDto> getAvailableVoyages();
    List<VoyageDto> searchAvailableVoyagesByRoute(String lieuDepart, String lieuArrivee, LocalDateTime dateDepart);
    VoyageDto createVoyage(VoyageCreateDto voyageCreateDto);
    Optional<VoyageDto> updateVoyage(Long id, VoyageCreateDto voyageCreateDto);
    boolean deleteVoyage(Long id);
    boolean updatePlacesDisponibles(Long voyageId, int nombrePlaces);
    boolean changeStatutVoyage(Long voyageId, StatutVoyage nouveauStatut);
    List<VoyageDto> getVoyagesByDateDepartBetween(LocalDateTime startDate, LocalDateTime endDate);

    Page<VoyageDto> getAllVoyages(Pageable pageable);
    Page<VoyageDto> getVoyagesByStatut(StatutVoyage statut, Pageable pageable);
    Page<VoyageDto> searchVoyagesByLieuDepart(String lieuDepart, Pageable pageable);
    Page<VoyageDto> searchVoyagesByLieuArrivee(String lieuArrivee, Pageable pageable);
    Page<VoyageDto> searchVoyagesByRoute(String lieuDepart, String lieuArrivee, Pageable pageable);
    Page<VoyageDto> getVoyagesByDateRange(LocalDateTime dateDepart, LocalDateTime dateRetour, Pageable pageable);
    Page<VoyageDto> getAvailableVoyages(Pageable pageable);
    Page<VoyageDto> searchAvailableVoyagesByRoute(String lieuDepart, String lieuArrivee, LocalDateTime dateDepart, Pageable pageable);
    Page<VoyageDto> getVoyagesByDateDepartBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<VoyageDto> searchVoyagesWithFilters(VoyageFilterDto filterDto, Pageable pageable);

    Long getTotalVoyages();
    Long getVoyagesByStatutCount(StatutVoyage statut);
    Long getAvailableVoyagesCount();

    Page<VoyageDto> searchGlobal(String query, Pageable pageable);
}