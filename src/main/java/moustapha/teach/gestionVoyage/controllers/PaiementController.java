package moustapha.teach.gestionVoyage.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import moustapha.teach.gestionVoyage.dto.*;
import moustapha.teach.gestionVoyage.enums.MethodePaiement;
import moustapha.teach.gestionVoyage.enums.StatutPaiement;
import moustapha.teach.gestionVoyage.services.PaiementService;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/paiements")

@Validated
@CrossOrigin(origins = "*")
@Tag(name = "Paiements", description = "API de gestion des paiements")
public class PaiementController {

    private final PaiementService paiementService;
    public PaiementController(final PaiementService paiementService){
        this.paiementService = paiementService;
    }

    @GetMapping
    @Operation(summary = "Obtenir tous les paiements", description = "Retourne la liste de tous les paiements")
    @ApiResponse(responseCode = "200", description = "Liste des paiements récupérée avec succès")
    public ResponseEntity<List<PaiementDto>> getAllPaiements() {
        List<PaiementDto> paiements = paiementService.getAllPaiements();
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un paiement par ID", description = "Retourne un paiement spécifique par son identifiant")
    @ApiResponse(responseCode = "200", description = "Paiement trouvé")
    @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    public ResponseEntity<PaiementDto> getPaiementById(
            @Parameter(description = "ID du paiement", required = true, example = "1") @PathVariable Long id) {
        return paiementService.getPaiementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Obtenir un paiement par ID de réservation", description = "Retourne le paiement associé à une réservation")
    @ApiResponse(responseCode = "200", description = "Paiement trouvé")
    @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    public ResponseEntity<PaiementDto> getPaiementByReservationId(
            @Parameter(description = "ID de la réservation", required = true, example = "1") @PathVariable Long reservationId) {
        return paiementService.getPaiementByReservationId(reservationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Obtenir les paiements par statut", description = "Retourne les paiements filtrés par statut")
    @ApiResponse(responseCode = "200", description = "Paiements récupérés avec succès")
    public ResponseEntity<List<PaiementDto>> getPaiementsByStatut(
            @Parameter(description = "Statut du paiement", required = true, example = "PAYE") @PathVariable StatutPaiement statut) {
        List<PaiementDto> paiements = paiementService.getPaiementsByStatut(statut);
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/methode/{methode}")
    @Operation(summary = "Obtenir les paiements par méthode", description = "Retourne les paiements filtrés par méthode de paiement")
    @ApiResponse(responseCode = "200", description = "Paiements récupérés avec succès")
    public ResponseEntity<List<PaiementDto>> getPaiementsByMethode(
            @Parameter(description = "Méthode de paiement", required = true, example = "CARTE_CREDIT") @PathVariable MethodePaiement methode) {
        List<PaiementDto> paiements = paiementService.getPaiementsByMethode(methode);
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtenir les paiements par utilisateur", description = "Retourne les paiements d'un utilisateur spécifique")
    @ApiResponse(responseCode = "200", description = "Paiements récupérés avec succès")
    public ResponseEntity<List<PaiementDto>> getPaiementsByUserId(
            @Parameter(description = "ID de l'utilisateur", required = true, example = "1") @PathVariable Long userId) {
        List<PaiementDto> paiements = paiementService.getPaiementsByUserId(userId);
        return ResponseEntity.ok(paiements);
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau paiement", description = "Crée un nouveau paiement pour une réservation")
    @ApiResponse(responseCode = "201", description = "Paiement créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> createPaiement(
            @Parameter(description = "Données du paiement à créer", required = true) @Valid @RequestBody PaiementCreateDto paiementCreateDto) {
        try {
            PaiementDto paiement = paiementService.createPaiement(paiementCreateDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(paiement);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/statut")
    @Operation(summary = "Mettre à jour le statut d'un paiement", description = "Modifie le statut d'un paiement existant")
    @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    public ResponseEntity<PaiementDto> updatePaiementStatut(
            @Parameter(description = "ID du paiement", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Nouveau statut", required = true, example = "PAYE") @RequestParam StatutPaiement statut) {
        return paiementService.updatePaiementStatut(id, statut)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un paiement", description = "Supprime un paiement existant")
    @ApiResponse(responseCode = "204", description = "Paiement supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    public ResponseEntity<Void> deletePaiement(
            @Parameter(description = "ID du paiement", required = true, example = "1") @PathVariable Long id) {
        if (paiementService.deletePaiement(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/paginated")
    @Operation(summary = "Obtenir tous les paiements (paginated)", description = "Retourne la liste paginée de tous les paiements")
    @ApiResponse(responseCode = "200", description = "Paiements paginés récupérés avec succès")
    public ResponseEntity<Page<PaiementDto>> getAllPaiementsPaginated(
            @Parameter(description = "Numéro de page (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "datePaiement") @RequestParam(defaultValue = "datePaiement") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc") @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PaiementDto> paiements = paiementService.getAllPaiements(pageable);
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/statut/{statut}/paginated")
    @Operation(summary = "Obtenir les paiements par statut (paginated)", description = "Retourne les paiements paginés filtrés par statut")
    @ApiResponse(responseCode = "200", description = "Paiements par statut paginés récupérés avec succès")
    public ResponseEntity<Page<PaiementDto>> getPaiementsByStatutPaginated(
            @Parameter(description = "Statut du paiement", required = true, example = "PAYE") @PathVariable StatutPaiement statut,
            @Parameter(description = "Numéro de page (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "datePaiement") @RequestParam(defaultValue = "datePaiement") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc") @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PaiementDto> paiements = paiementService.getPaiementsByStatut(statut, pageable);
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/methode/{methode}/paginated")
    @Operation(summary = "Obtenir les paiements par méthode (paginated)", description = "Retourne les paiements paginés filtrés par méthode")
    @ApiResponse(responseCode = "200", description = "Paiements par méthode paginés récupérés avec succès")
    public ResponseEntity<Page<PaiementDto>> getPaiementsByMethodePaginated(
            @Parameter(description = "Méthode de paiement", required = true, example = "CARTE_CREDIT") @PathVariable MethodePaiement methode,
            @Parameter(description = "Numéro de page (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "datePaiement") @RequestParam(defaultValue = "datePaiement") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc") @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PaiementDto> paiements = paiementService.getPaiementsByMethode(methode, pageable);
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/reservation/{reservationId}/paginated")
    @Operation(summary = "Obtenir les paiements par réservation (paginated)", description = "Retourne les paiements paginés d'une réservation")
    @ApiResponse(responseCode = "200", description = "Paiements par réservation paginés récupérés avec succès")
    public ResponseEntity<Page<PaiementDto>> getPaiementsByReservationIdPaginated(
            @Parameter(description = "ID de la réservation", required = true, example = "1") @PathVariable Long reservationId,
            @Parameter(description = "Numéro de page (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "datePaiement") @RequestParam(defaultValue = "datePaiement") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc") @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PaiementDto> paiements = paiementService.getPaiementsByReservationId(reservationId, pageable);
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/user/{userId}/paginated")
    @Operation(summary = "Obtenir les paiements par utilisateur (paginated)", description = "Retourne les paiements paginés d'un utilisateur")
    @ApiResponse(responseCode = "200", description = "Paiements par utilisateur paginés récupérés avec succès")
    public ResponseEntity<Page<PaiementDto>> getPaiementsByUserIdPaginated(
            @Parameter(description = "ID de l'utilisateur", required = true, example = "1") @PathVariable Long userId,
            @Parameter(description = "Numéro de page (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "datePaiement") @RequestParam(defaultValue = "datePaiement") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc") @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PaiementDto> paiements = paiementService.getPaiementsByUserId(userId, pageable);
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/date-range/paginated")
    @Operation(summary = "Obtenir les paiements par plage de dates (paginated)", description = "Retourne les paiements paginés dans une période")
    @ApiResponse(responseCode = "200", description = "Paiements par date paginés récupérés avec succès")
    public ResponseEntity<Page<PaiementDto>> getPaiementsByDateRangePaginated(
            @Parameter(description = "Date de début", required = true, example = "2024-01-01T00:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Date de fin", required = true, example = "2024-12-31T23:59:59") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Numéro de page (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "datePaiement") @RequestParam(defaultValue = "datePaiement") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc") @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PaiementDto> paiements = paiementService.getPaiementsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/montant-range/paginated")
    @Operation(summary = "Obtenir les paiements par plage de montant (paginated)", description = "Retourne les paiements paginés dans une plage de montant")
    @ApiResponse(responseCode = "200", description = "Paiements par montant paginés récupérés avec succès")
    public ResponseEntity<Page<PaiementDto>> getPaiementsByMontantRangePaginated(
            @Parameter(description = "Montant minimum", required = true, example = "0") @RequestParam BigDecimal minAmount,
            @Parameter(description = "Montant maximum", required = true, example = "1000") @RequestParam BigDecimal maxAmount,
            @Parameter(description = "Numéro de page (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "montant") @RequestParam(defaultValue = "montant") String sortBy,
            @Parameter(description = "Direction de tri", example = "asc") @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PaiementDto> paiements = paiementService.getPaiementsByMontantRange(minAmount, maxAmount, pageable);
        return ResponseEntity.ok(paiements);
    }

    @PostMapping("/search")
    @Operation(summary = "Recherche avancée de paiements", description = "Recherche de paiements avec filtres multiples et pagination")
    @ApiResponse(responseCode = "200", description = "Paiements filtrés récupérés avec succès")
    public ResponseEntity<Page<PaiementDto>> searchPaiementsWithFilters(
            @Parameter(description = "Critères de filtrage", required = true) @RequestBody PaiementFilterDto filterDto) {

        Sort sort = Sort.by(Sort.Direction.fromString(filterDto.getSortDirection()), filterDto.getSortBy());
        Pageable pageable = PageRequest.of(filterDto.getPage(), filterDto.getSize(), sort);

        Page<PaiementDto> paiements = paiementService.searchPaiementsWithFilters(filterDto, pageable);
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/{id}/detail")
    @Operation(summary = "Obtenir les détails d'un paiement", description = "Retourne les détails complets d'un paiement")
    @ApiResponse(responseCode = "200", description = "Détails du paiement récupérés avec succès")
    @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    public ResponseEntity<PaiementDetailDto> getPaiementDetail(
            @Parameter(description = "ID du paiement", required = true, example = "1") @PathVariable Long id) {
        return paiementService.getPaiementDetail(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ENDPOINTS DE STATISTIQUES

    @GetMapping("/stats/total")
    @Operation(summary = "Nombre total de paiements", description = "Retourne le nombre total de paiements")
    @ApiResponse(responseCode = "200", description = "Nombre total de paiements")
    public ResponseEntity<Long> getTotalPaiements() {
        Long count = paiementService.getTotalPaiements();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/statut/{statut}")
    @Operation(summary = "Nombre de paiements par statut", description = "Retourne le nombre de paiements pour un statut donné")
    @ApiResponse(responseCode = "200", description = "Nombre de paiements par statut")
    public ResponseEntity<Long> getPaiementsByStatutCount(
            @Parameter(description = "Statut du paiement", required = true, example = "PAYE") @PathVariable StatutPaiement statut) {
        Long count = paiementService.getPaiementsByStatutCount(statut);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/methode/{methode}")
    @Operation(summary = "Nombre de paiements par méthode", description = "Retourne le nombre de paiements pour une méthode donnée")
    @ApiResponse(responseCode = "200", description = "Nombre de paiements par méthode")
    public ResponseEntity<Long> getPaiementsByMethodeCount(
            @Parameter(description = "Méthode de paiement", required = true, example = "CARTE_CREDIT") @PathVariable MethodePaiement methode) {
        Long count = paiementService.getPaiementsByMethodeCount(methode);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/user/{userId}")
    @Operation(summary = "Nombre de paiements par utilisateur", description = "Retourne le nombre de paiements pour un utilisateur donné")
    @ApiResponse(responseCode = "200", description = "Nombre de paiements par utilisateur")
    public ResponseEntity<Long> getPaiementsByUserIdCount(
            @Parameter(description = "ID de l'utilisateur", required = true, example = "1") @PathVariable Long userId) {
        Long count = paiementService.getPaiementsByUserIdCount(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/revenue/total")
    @Operation(summary = "Revenu total", description = "Retourne le revenu total des paiements validés")
    @ApiResponse(responseCode = "200", description = "Revenu total")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        BigDecimal revenue = paiementService.getTotalRevenue();
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/stats/revenue/date-range")
    @Operation(summary = "Revenu par plage de dates", description = "Retourne le revenu des paiements validés dans une période")
    @ApiResponse(responseCode = "200", description = "Revenu par période")
    public ResponseEntity<BigDecimal> getRevenueBetweenDates(
            @Parameter(description = "Date de début", required = true, example = "2024-01-01T00:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Date de fin", required = true, example = "2024-12-31T23:59:59") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal revenue = paiementService.getRevenueBetweenDates(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }
}