package moustapha.teach.gestionVoyage.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import moustapha.teach.gestionVoyage.dto.VoyageDto;
import moustapha.teach.gestionVoyage.dto.VoyageCreateDto;
import moustapha.teach.gestionVoyage.dto.VoyageFilterDto;
import moustapha.teach.gestionVoyage.enums.StatutVoyage;
import moustapha.teach.gestionVoyage.services.VoyageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/voyages")
@Validated
@CrossOrigin(origins = "*")
@Tag(name = "Voyages", description = "API de gestion des voyages")
public class VoyageController {

    private final VoyageService voyageService;

    public VoyageController(final VoyageService voyageService){
        this.voyageService = voyageService;
    }

    @GetMapping("/all")
    @Operation(summary = "Obtenir tous les voyages", description = "Retourne la liste de tous les voyages")
    @ApiResponse(responseCode = "200", description = "Liste des voyages récupérée avec succès")
    public ResponseEntity<List<VoyageDto>> getAllVoyages() {
        List<VoyageDto> voyages = voyageService.getAllVoyages();
        return ResponseEntity.ok(voyages);
    }

    @GetMapping("/details/{id}")
    @Operation(summary = "Obtenir les détails complets d'un voyage", description = "Retourne tous les détails d'un voyage spécifique")
    @ApiResponse(responseCode = "200", description = "Détails du voyage trouvé")
    @ApiResponse(responseCode = "404", description = "Voyage non trouvé")
    public ResponseEntity<VoyageDto> getDetailVoyage(
            @Parameter(description = "ID du voyage", required = true, example = "1") @PathVariable Long id) {
        return voyageService.getVoyageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un voyage par ID", description = "Retourne un voyage spécifique par son identifiant")
    @ApiResponse(responseCode = "200", description = "Voyage trouvé")
    @ApiResponse(responseCode = "404", description = "Voyage non trouvé")
    public ResponseEntity<VoyageDto> getVoyageById(
            @Parameter(description = "ID du voyage", required = true, example = "1") @PathVariable Long id) {
        return voyageService.getVoyageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Obtenir les voyages par statut", description = "Retourne les voyages filtrés par statut")
    @ApiResponse(responseCode = "200", description = "Voyages récupérés avec succès")
    public ResponseEntity<List<VoyageDto>> getVoyagesByStatut(
            @Parameter(description = "Statut du voyage", required = true, example = "PLANIFIE") @PathVariable StatutVoyage statut) {
        List<VoyageDto> voyages = voyageService.getVoyagesByStatut(statut);
        return ResponseEntity.ok(voyages);
    }

    @GetMapping("/available")
    @Operation(summary = "Obtenir les voyages disponibles", description = "Retourne les voyages ayant des places disponibles")
    @ApiResponse(responseCode = "200", description = "Voyages disponibles récupérés avec succès")
    public ResponseEntity<List<VoyageDto>> getAvailableVoyages() {
        List<VoyageDto> voyages = voyageService.getAvailableVoyages();
        return ResponseEntity.ok(voyages);
    }

    @GetMapping("/search/depart")
    @Operation(summary = "Rechercher des voyages par lieu de départ", description = "Retourne les voyages filtrés par lieu de départ")
    @ApiResponse(responseCode = "200", description = "Voyages récupérés avec succès")
    public ResponseEntity<List<VoyageDto>> searchVoyagesByLieuDepart(
            @Parameter(description = "Lieu de départ", required = true, example = "Dakar") @RequestParam String lieuDepart) {
        List<VoyageDto> voyages = voyageService.searchVoyagesByLieuDepart(lieuDepart);
        return ResponseEntity.ok(voyages);
    }

    @GetMapping("/search/arrivee")
    @Operation(summary = "Rechercher des voyages par lieu d'arrivée", description = "Retourne les voyages filtrés par lieu d'arrivée")
    @ApiResponse(responseCode = "200", description = "Voyages récupérés avec succès")
    public ResponseEntity<List<VoyageDto>> searchVoyagesByLieuArrivee(
            @Parameter(description = "Lieu d'arrivée", required = true, example = "Touba") @RequestParam String lieuArrivee) {
        List<VoyageDto> voyages = voyageService.searchVoyagesByLieuArrivee(lieuArrivee);
        return ResponseEntity.ok(voyages);
    }

    @GetMapping("/search/route")
    @Operation(summary = "Rechercher des voyages par itinéraire", description = "Retourne les voyages filtrés par lieu de départ et d'arrivée")
    @ApiResponse(responseCode = "200", description = "Voyages récupérés avec succès")
    public ResponseEntity<List<VoyageDto>> searchVoyagesByRoute(
            @Parameter(description = "Lieu de départ", required = true, example = "Dakar") @RequestParam String lieuDepart,
            @Parameter(description = "Lieu d'arrivée", required = true, example = "Touba") @RequestParam String lieuArrivee) {
        List<VoyageDto> voyages = voyageService.searchVoyagesByRoute(lieuDepart, lieuArrivee);
        return ResponseEntity.ok(voyages);
    }

    @GetMapping("/search/available-route")
    @Operation(summary = "Rechercher des voyages disponibles par itinéraire et date", description = "Retourne les voyages disponibles filtrés par lieu de départ, d'arrivée et date de départ")
    @ApiResponse(responseCode = "200", description = "Voyages récupérés avec succès")
    public ResponseEntity<List<VoyageDto>> searchAvailableVoyagesByRoute(
            @Parameter(description = "Lieu de départ", required = true, example = "Dakar") @RequestParam String lieuDepart,
            @Parameter(description = "Lieu d'arrivée", required = true, example = "Touba") @RequestParam String lieuArrivee,
            @Parameter(description = "Date de départ", required = true, example = "2024-12-01T08:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDepart) {
        List<VoyageDto> voyages = voyageService.searchAvailableVoyagesByRoute(lieuDepart, lieuArrivee, dateDepart);
        return ResponseEntity.ok(voyages);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Obtenir les voyages par plage de dates", description = "Retourne les voyages dont la date de départ et de retour sont dans la plage spécifiée")
    @ApiResponse(responseCode = "200", description = "Voyages récupérés avec succès")
    public ResponseEntity<List<VoyageDto>> getVoyagesByDateRange(
            @Parameter(description = "Date de début", required = true, example = "2024-12-01T00:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDepart,
            @Parameter(description = "Date de fin", required = true, example = "2024-12-31T23:59:59") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateRetour) {
        List<VoyageDto> voyages = voyageService.getVoyagesByDateRange(dateDepart, dateRetour);
        return ResponseEntity.ok(voyages);
    }

    @GetMapping("/depart-between")
    @Operation(summary = "Obtenir les voyages par intervalle de dates de départ", description = "Retourne les voyages dont la date de départ est comprise entre deux dates")
    @ApiResponse(responseCode = "200", description = "Voyages récupérés avec succès")
    public ResponseEntity<List<VoyageDto>> getVoyagesByDateDepartBetween(
            @Parameter(description = "Date de début", required = true, example = "2024-12-01T00:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Date de fin", required = true, example = "2024-12-31T23:59:59") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<VoyageDto> voyages = voyageService.getVoyagesByDateDepartBetween(startDate, endDate);
        return ResponseEntity.ok(voyages);
    }

    // Méthodes avec pagination
    @GetMapping
    @Operation(summary = "Obtenir tous les voyages avec pagination", description = "Retourne la liste paginée de tous les voyages")
    @ApiResponse(responseCode = "200", description = "Page de voyages récupérée avec succès")
    public ResponseEntity<Page<VoyageDto>> getAllVoyages(
            @Parameter(description = "Numéro de page", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateDepart") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri", example = "asc") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<VoyageDto> voyagesPage = voyageService.getAllVoyages(pageable);
        return ResponseEntity.ok(voyagesPage);
    }

    @GetMapping("/statut-paginated/{statut}")
    @Operation(summary = "Obtenir les voyages par statut avec pagination", description = "Retourne les voyages filtrés par statut avec pagination")
    @ApiResponse(responseCode = "200", description = "Page de voyages récupérée avec succès")
    public ResponseEntity<Page<VoyageDto>> getVoyagesByStatut(
            @Parameter(description = "Statut du voyage", required = true, example = "PLANIFIE") @PathVariable StatutVoyage statut,
            @Parameter(description = "Numéro de page", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateDepart") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri", example = "asc") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<VoyageDto> voyagesPage = voyageService.getVoyagesByStatut(statut, pageable);
        return ResponseEntity.ok(voyagesPage);
    }

    @GetMapping("/available-paginated")
    @Operation(summary = "Obtenir les voyages disponibles avec pagination", description = "Retourne les voyages ayant des places disponibles avec pagination")
    @ApiResponse(responseCode = "200", description = "Page de voyages disponibles récupérée avec succès")
    public ResponseEntity<Page<VoyageDto>> getAvailableVoyages(
            @Parameter(description = "Numéro de page", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateDepart") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri", example = "asc") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<VoyageDto> voyagesPage = voyageService.getAvailableVoyages(pageable);
        return ResponseEntity.ok(voyagesPage);
    }

    @GetMapping("/search/depart-paginated")
    @Operation(summary = "Rechercher des voyages par lieu de départ avec pagination", description = "Retourne les voyages filtrés par lieu de départ avec pagination")
    @ApiResponse(responseCode = "200", description = "Page de voyages récupérée avec succès")
    public ResponseEntity<Page<VoyageDto>> searchVoyagesByLieuDepart(
            @Parameter(description = "Lieu de départ", required = true, example = "Dakar") @RequestParam String lieuDepart,
            @Parameter(description = "Numéro de page", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateDepart") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri", example = "asc") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<VoyageDto> voyagesPage = voyageService.searchVoyagesByLieuDepart(lieuDepart, pageable);
        return ResponseEntity.ok(voyagesPage);
    }

    @GetMapping("/search/arrivee-paginated")
    @Operation(summary = "Rechercher des voyages par lieu d'arrivée avec pagination", description = "Retourne les voyages filtrés par lieu d'arrivée avec pagination")
    @ApiResponse(responseCode = "200", description = "Page de voyages récupérée avec succès")
    public ResponseEntity<Page<VoyageDto>> searchVoyagesByLieuArrivee(
            @Parameter(description = "Lieu d'arrivée", required = true, example = "Touba") @RequestParam String lieuArrivee,
            @Parameter(description = "Numéro de page", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateDepart") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri", example = "asc") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<VoyageDto> voyagesPage = voyageService.searchVoyagesByLieuArrivee(lieuArrivee, pageable);
        return ResponseEntity.ok(voyagesPage);
    }

    @GetMapping("/search/route-paginated")
    @Operation(summary = "Rechercher des voyages par itinéraire avec pagination", description = "Retourne les voyages filtrés par lieu de départ et d'arrivée avec pagination")
    @ApiResponse(responseCode = "200", description = "Page de voyages récupérée avec succès")
    public ResponseEntity<Page<VoyageDto>> searchVoyagesByRoute(
            @Parameter(description = "Lieu de départ", required = true, example = "Dakar") @RequestParam String lieuDepart,
            @Parameter(description = "Lieu d'arrivée", required = true, example = "Touba") @RequestParam String lieuArrivee,
            @Parameter(description = "Numéro de page", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateDepart") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri", example = "asc") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<VoyageDto> voyagesPage = voyageService.searchVoyagesByRoute(lieuDepart, lieuArrivee, pageable);
        return ResponseEntity.ok(voyagesPage);
    }

    @GetMapping("/search/available-route-paginated")
    @Operation(summary = "Rechercher des voyages disponibles par itinéraire et date avec pagination", description = "Retourne les voyages disponibles filtrés par lieu de départ, d'arrivée et date de départ avec pagination")
    @ApiResponse(responseCode = "200", description = "Page de voyages récupérée avec succès")
    public ResponseEntity<Page<VoyageDto>> searchAvailableVoyagesByRoute(
            @Parameter(description = "Lieu de départ", required = true, example = "Dakar") @RequestParam String lieuDepart,
            @Parameter(description = "Lieu d'arrivée", required = true, example = "Touba") @RequestParam String lieuArrivee,
            @Parameter(description = "Date de départ", required = true, example = "2024-12-01T08:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDepart,
            @Parameter(description = "Numéro de page", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateDepart") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri", example = "asc") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<VoyageDto> voyagesPage = voyageService.searchAvailableVoyagesByRoute(lieuDepart, lieuArrivee, dateDepart, pageable);
        return ResponseEntity.ok(voyagesPage);
    }

    @GetMapping("/date-range-paginated")
    @Operation(summary = "Obtenir les voyages par plage de dates avec pagination", description = "Retourne les voyages dont les dates sont dans la plage spécifiée avec pagination")
    @ApiResponse(responseCode = "200", description = "Page de voyages récupérée avec succès")
    public ResponseEntity<Page<VoyageDto>> getVoyagesByDateRange(
            @Parameter(description = "Date de début", required = true, example = "2024-12-01T00:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDepart,
            @Parameter(description = "Date de fin", required = true, example = "2024-12-31T23:59:59") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateRetour,
            @Parameter(description = "Numéro de page", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateDepart") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri", example = "asc") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<VoyageDto> voyagesPage = voyageService.getVoyagesByDateRange(dateDepart, dateRetour, pageable);
        return ResponseEntity.ok(voyagesPage);
    }

    @GetMapping("/depart-between-paginated")
    @Operation(summary = "Obtenir les voyages par intervalle de dates de départ avec pagination", description = "Retourne les voyages dont la date de départ est comprise entre deux dates avec pagination")
    @ApiResponse(responseCode = "200", description = "Page de voyages récupérée avec succès")
    public ResponseEntity<Page<VoyageDto>> getVoyagesByDateDepartBetween(
            @Parameter(description = "Date de début", required = true, example = "2024-12-01T00:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Date de fin", required = true, example = "2024-12-31T23:59:59") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Numéro de page", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateDepart") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri", example = "asc") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<VoyageDto> voyagesPage = voyageService.getVoyagesByDateDepartBetween(startDate, endDate, pageable);
        return ResponseEntity.ok(voyagesPage);
    }

    @PostMapping("/search/filters")
    @Operation(summary = "Rechercher des voyages avec filtres avancés", description = "Retourne les voyages filtrés selon plusieurs critères avec pagination")
    @ApiResponse(responseCode = "200", description = "Page de voyages récupérée avec succès")
    public ResponseEntity<Page<VoyageDto>> searchVoyagesWithFilters(
            @Parameter(description = "Filtres de recherche", required = true) @RequestBody VoyageFilterDto filterDto,
            @Parameter(description = "Numéro de page", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateDepart") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri", example = "asc") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<VoyageDto> voyagesPage = voyageService.searchVoyagesWithFilters(filterDto, pageable);
        return ResponseEntity.ok(voyagesPage);
    }

    // Méthodes CRUD
    @PostMapping
    @Operation(summary = "Créer un nouveau voyage", description = "Crée un nouveau voyage avec les informations fournies")
    @ApiResponse(responseCode = "201", description = "Voyage créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> createVoyage(
            @Parameter(description = "Données du voyage à créer", required = true) @Valid @RequestBody VoyageCreateDto voyageCreateDto) {
        try {
            VoyageDto voyage = voyageService.createVoyage(voyageCreateDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(voyage);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un voyage", description = "Modifie les informations d'un voyage existant")
    @ApiResponse(responseCode = "200", description = "Voyage mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Voyage non trouvé")
    public ResponseEntity<VoyageDto> updateVoyage(
            @Parameter(description = "ID du voyage", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Nouvelles données du voyage", required = true) @Valid @RequestBody VoyageCreateDto voyageCreateDto) {
        return voyageService.updateVoyage(id, voyageCreateDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/statut")
    @Operation(summary = "Modifier le statut d'un voyage", description = "Change le statut d'un voyage existant")
    @ApiResponse(responseCode = "200", description = "Statut du voyage mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Voyage non trouvé")
    public ResponseEntity<?> changeStatutVoyage(
            @Parameter(description = "ID du voyage", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Nouveau statut", required = true, example = "ANNULE") @RequestParam StatutVoyage statut) {
        if (voyageService.changeStatutVoyage(id, statut)) {
            return ResponseEntity.ok("Statut du voyage mis à jour avec succès");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voyage non trouvé");
    }

    @PutMapping("/{id}/places")
    @Operation(summary = "Mettre à jour les places disponibles", description = "Modifie le nombre de places disponibles pour un voyage")
    @ApiResponse(responseCode = "200", description = "Nombre de places mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Voyage non trouvé")
    public ResponseEntity<?> updatePlacesDisponibles(
            @Parameter(description = "ID du voyage", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Nouveau nombre de places", required = true, example = "45") @RequestParam int places) {
        if (voyageService.updatePlacesDisponibles(id, places)) {
            return ResponseEntity.ok("Nombre de places mis à jour avec succès");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voyage non trouvé");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un voyage", description = "Supprime un voyage existant")
    @ApiResponse(responseCode = "204", description = "Voyage supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Voyage non trouvé")
    public ResponseEntity<Void> deleteVoyage(
            @Parameter(description = "ID du voyage", required = true, example = "1") @PathVariable Long id) {
        if (voyageService.deleteVoyage(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Méthodes de statistiques
    @GetMapping("/stats/total")
    @Operation(summary = "Obtenir le nombre total de voyages", description = "Retourne le nombre total de voyages")
    @ApiResponse(responseCode = "200", description = "Nombre total récupéré avec succès")
    public ResponseEntity<Long> getTotalVoyages() {
        Long total = voyageService.getTotalVoyages();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/stats/statut/{statut}")
    @Operation(summary = "Obtenir le nombre de voyages par statut", description = "Retourne le nombre de voyages pour un statut donné")
    @ApiResponse(responseCode = "200", description = "Nombre récupéré avec succès")
    public ResponseEntity<Long> getVoyagesByStatutCount(
            @Parameter(description = "Statut du voyage", required = true, example = "PLANIFIE") @PathVariable StatutVoyage statut) {
        Long count = voyageService.getVoyagesByStatutCount(statut);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/available")
    @Operation(summary = "Obtenir le nombre de voyages disponibles", description = "Retourne le nombre de voyages ayant des places disponibles")
    @ApiResponse(responseCode = "200", description = "Nombre récupéré avec succès")
    public ResponseEntity<Long> getAvailableVoyagesCount() {
        Long count = voyageService.getAvailableVoyagesCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/search/global")
    @Operation(summary = "Recherche globale", description = "Recherche dans tous les champs des voyages")
    @ApiResponse(responseCode = "200", description = "Voyages trouvés")
    public ResponseEntity<Page<VoyageDto>> searchGlobal(
            @Parameter(description = "Terme de recherche", required = true) @RequestParam String query,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<VoyageDto> voyagesPage = voyageService.searchGlobal(query, pageable);
        return ResponseEntity.ok(voyagesPage);
    }

}