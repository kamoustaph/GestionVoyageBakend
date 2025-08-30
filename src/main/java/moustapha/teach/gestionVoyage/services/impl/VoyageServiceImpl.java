package moustapha.teach.gestionVoyage.services.impl;


import lombok.extern.slf4j.Slf4j;
import moustapha.teach.gestionVoyage.dto.VoyageDto;
import moustapha.teach.gestionVoyage.dto.VoyageCreateDto;
import moustapha.teach.gestionVoyage.dto.VoyageFilterDto;
import moustapha.teach.gestionVoyage.entities.Voyage;
import moustapha.teach.gestionVoyage.mapper.VoyageMapper;
import moustapha.teach.gestionVoyage.repositories.VoyageRepository;
import moustapha.teach.gestionVoyage.enums.StatutVoyage;
import moustapha.teach.gestionVoyage.services.VoyageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class VoyageServiceImpl implements VoyageService {

    private final VoyageRepository voyageRepository;
    private final VoyageMapper voyageMapper;

    public  VoyageServiceImpl(final VoyageRepository voyageRepository,
                              final VoyageMapper voyageMapper)
                              {
        this.voyageRepository=voyageRepository;
        this.voyageMapper=voyageMapper;
    }

    // Méthodes existantes (sans pagination)
    @Override
    @Transactional(readOnly = true)
    public List<VoyageDto> getAllVoyages() {
        return voyageRepository.findAll()
                .stream()
                .map(voyageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VoyageDto> getVoyageById(Long id) {
        return voyageRepository.findById(id)
                .map(voyageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoyageDto> getVoyagesByStatut(StatutVoyage statut) {
        return voyageRepository.findByStatut(statut)
                .stream()
                .map(voyageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoyageDto> searchVoyagesByLieuDepart(String lieuDepart) {
        return voyageRepository.findByLieuDepartContainingIgnoreCase(lieuDepart)
                .stream()
                .map(voyageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoyageDto> searchVoyagesByLieuArrivee(String lieuArrivee) {
        return voyageRepository.findByLieuArriveeContainingIgnoreCase(lieuArrivee)
                .stream()
                .map(voyageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoyageDto> searchVoyagesByRoute(String lieuDepart, String lieuArrivee) {
        return voyageRepository.findByLieuDepartAndLieuArrivee(lieuDepart, lieuArrivee)
                .stream()
                .map(voyageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoyageDto> getVoyagesByDateRange(LocalDateTime dateDepart, LocalDateTime dateRetour) {
        return voyageRepository.findByDateRange(dateDepart, dateRetour)
                .stream()
                .map(voyageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoyageDto> getAvailableVoyages() {
        return voyageRepository.findAvailableVoyages()
                .stream()
                .map(voyageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoyageDto> searchAvailableVoyagesByRoute(String lieuDepart, String lieuArrivee, LocalDateTime dateDepart) {
        return voyageRepository.findAvailableVoyagesByRoute(lieuDepart, lieuArrivee, dateDepart)
                .stream()
                .map(voyageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoyageDto> getVoyagesByDateDepartBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return voyageRepository.findVoyagesByDateDepartBetween(startDate, endDate)
                .stream()
                .map(voyageMapper::toDto)
                .collect(Collectors.toList());
    }

    // Nouvelles méthodes avec pagination
    @Override
    @Transactional(readOnly = true)
    public Page<VoyageDto> getAllVoyages(Pageable pageable) {
        Page<Voyage> voyagePage = voyageRepository.findAll(pageable);
        return voyagePage.map(voyageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoyageDto> getVoyagesByStatut(StatutVoyage statut, Pageable pageable) {
        Page<Voyage> voyagePage = voyageRepository.findByStatut(statut, pageable);
        return voyagePage.map(voyageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoyageDto> searchVoyagesByLieuDepart(String lieuDepart, Pageable pageable) {
        Page<Voyage> voyagePage = voyageRepository.findByLieuDepartContainingIgnoreCase(lieuDepart, pageable);
        return voyagePage.map(voyageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoyageDto> searchVoyagesByLieuArrivee(String lieuArrivee, Pageable pageable) {
        Page<Voyage> voyagePage = voyageRepository.findByLieuArriveeContainingIgnoreCase(lieuArrivee, pageable);
        return voyagePage.map(voyageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoyageDto> searchVoyagesByRoute(String lieuDepart, String lieuArrivee, Pageable pageable) {
        Page<Voyage> voyagePage = voyageRepository.findByLieuDepartAndLieuArrivee(lieuDepart, lieuArrivee, pageable);
        return voyagePage.map(voyageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoyageDto> getVoyagesByDateRange(LocalDateTime dateDepart, LocalDateTime dateRetour, Pageable pageable) {
        Page<Voyage> voyagePage = voyageRepository.findByDateRange(dateDepart, dateRetour, pageable);
        return voyagePage.map(voyageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoyageDto> getAvailableVoyages(Pageable pageable) {
        Page<Voyage> voyagePage = voyageRepository.findAvailableVoyages(pageable);
        return voyagePage.map(voyageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoyageDto> searchAvailableVoyagesByRoute(String lieuDepart, String lieuArrivee, LocalDateTime dateDepart, Pageable pageable) {
        Page<Voyage> voyagePage = voyageRepository.findAvailableVoyagesByRoute(lieuDepart, lieuArrivee, dateDepart, pageable);
        return voyagePage.map(voyageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoyageDto> getVoyagesByDateDepartBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<Voyage> voyagePage = voyageRepository.findVoyagesByDateDepartBetween(startDate, endDate, pageable);
        return voyagePage.map(voyageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoyageDto> searchVoyagesWithFilters(VoyageFilterDto filterDto, Pageable pageable) {
        log.info("Recherche de voyages avec filtres: {}", filterDto);

        Page<Voyage> voyagePage = voyageRepository.findVoyagesWithFilters(
                filterDto.getLieuDepart(),
                filterDto.getLieuArrivee(),
                filterDto.getStatut(),
                filterDto.getPrixMin(),
                filterDto.getPrixMax(),
                filterDto.getDateDepart(),
                filterDto.getDateRetour(),
                filterDto.getPlacesMin(),
                pageable
        );

        return voyagePage.map(voyageMapper::toDto);
    }

    // Méthodes CRUD existantes
    @Override
    public VoyageDto createVoyage(VoyageCreateDto voyageCreateDto) {
        log.info("Création d'un nouveau voyage de {} à {}", voyageCreateDto.getLieuDepart(), voyageCreateDto.getLieuArrivee());

        Voyage voyage = voyageMapper.toEntity(voyageCreateDto);
        Voyage savedVoyage = voyageRepository.save(voyage);

        log.info("Voyage créé avec l'ID {}", savedVoyage.getId());
        return voyageMapper.toDto(savedVoyage);
    }

    @Override
    public Optional<VoyageDto> updateVoyage(Long id, VoyageCreateDto voyageCreateDto) {
        return voyageRepository.findById(id)
                .map(voyage -> {
                    voyageMapper.updateEntity(voyageCreateDto, voyage);
                    return voyageMapper.toDto(voyageRepository.save(voyage));
                });
    }

    @Override
    public boolean deleteVoyage(Long id) {
        if (voyageRepository.existsById(id)) {
            voyageRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean updatePlacesDisponibles(Long voyageId, int nombrePlaces) {
        return voyageRepository.findById(voyageId)
                .map(voyage -> {
                    voyage.setPlacesDisponibles(nombrePlaces);
                    voyageRepository.save(voyage);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean changeStatutVoyage(Long voyageId, StatutVoyage nouveauStatut) {
        return voyageRepository.findById(voyageId)
                .map(voyage -> {
                    voyage.setStatut(nouveauStatut);
                    voyageRepository.save(voyage);
                    return true;
                })
                .orElse(false);
    }

    // Méthodes de statistiques
    @Override
    @Transactional(readOnly = true)
    public Long getTotalVoyages() {
        return voyageRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getVoyagesByStatutCount(StatutVoyage statut) {
        return voyageRepository.countByStatut(statut);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAvailableVoyagesCount() {
        return voyageRepository.countAvailableVoyages();
    }

    // Méthode utilitaire pour créer un Pageable avec tri
    private Pageable createPageableWithSort(VoyageFilterDto filterDto) {
        Sort.Direction direction = "desc".equalsIgnoreCase(filterDto.getSortDirection())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, filterDto.getSortBy());

        return PageRequest.of(filterDto.getPage(), filterDto.getSize(), sort);
    }



    @Override
    public Page<VoyageDto> searchGlobal(String query, Pageable pageable) {
        return voyageRepository.findByGlobalSearch(query, pageable)
                .map(voyageMapper::toDto);
    }
}