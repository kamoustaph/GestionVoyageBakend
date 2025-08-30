package moustapha.teach.gestionVoyage.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import moustapha.teach.gestionVoyage.dto.ReservationDetailDto;
import moustapha.teach.gestionVoyage.dto.ReservationDto;
import moustapha.teach.gestionVoyage.dto.ReservationCreateDto;
import moustapha.teach.gestionVoyage.enums.ReservationStatus;
import moustapha.teach.gestionVoyage.services.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import moustapha.teach.gestionVoyage.dto.ReservationFilterDto;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*")
@Tag(name = "📋 Réservations", description = "API de gestion des réservations de voyage")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @Operation(
            summary = "🔍 Obtenir toutes les réservations",
            description = "Récupère la liste complète de toutes les réservations dans le système"
    )
    @ApiResponse(responseCode = "200", description = "✅ Liste des réservations récupérée avec succès")
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        List<ReservationDto> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "🎯 Obtenir une réservation par ID",
            description = "Récupère les détails d'une réservation spécifique grâce à son identifiant unique"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "✅ Réservation trouvée et retournée"),
            @ApiResponse(responseCode = "404", description = "❌ Réservation non trouvée")
    })
    public ResponseEntity<ReservationDto> getReservationById(
            @Parameter(description = "Identifiant unique de la réservation", required = true, example = "1")
            @PathVariable Long id
    ) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/detail")
    @Operation(
            summary = "📋 Obtenir les détails d'une réservation",
            description = "Récupère les détails complets d'une réservation avec informations utilisateur et voyage"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "✅ Détails de la réservation récupérés avec succès"),
            @ApiResponse(responseCode = "404", description = "❌ Réservation non trouvée")
    })
    public ResponseEntity<ReservationDetailDto> getReservationDetail(
            @Parameter(description = "Identifiant unique de la réservation", required = true, example = "1")
            @PathVariable Long id
    ) {
        return reservationService.getReservationDetail(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }



    @GetMapping("/user/{userId}")
    @Operation(
            summary = "👤 Obtenir les réservations d'un utilisateur",
            description = "Récupère toutes les réservations effectuées par un utilisateur spécifique"
    )
    @ApiResponse(responseCode = "200", description = "✅ Réservations de l'utilisateur récupérées")
    public ResponseEntity<List<ReservationDto>> getReservationsByUserId(
            @Parameter(description = "Identifiant unique de l'utilisateur", required = true, example = "1")
            @PathVariable Long userId
    ) {
        List<ReservationDto> reservations = reservationService.getReservationsByUserId(userId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/voyage/{voyageId}")
    @Operation(
            summary = "✈️ Obtenir les réservations d'un voyage",
            description = "Récupère toutes les réservations associées à un voyage spécifique"
    )
    @ApiResponse(responseCode = "200", description = "✅ Réservations du voyage récupérées")
    public ResponseEntity<List<ReservationDto>> getReservationsByVoyageId(
            @Parameter(description = "Identifiant unique du voyage", required = true, example = "1")
            @PathVariable Long voyageId
    ) {
        List<ReservationDto> reservations = reservationService.getReservationsByVoyageId(voyageId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/statut/{statut}")
    @Operation(
            summary = "📊 Obtenir les réservations par statut",
            description = "Filtre les réservations selon leur statut (EN_ATTENTE, CONFIRMEE, ANNULEE, EXPIREE)"
    )
    @ApiResponse(responseCode = "200", description = "✅ Réservations filtrées par statut")
    public ResponseEntity<List<ReservationDto>> getReservationsByStatut(
            @Parameter(description = "Statut de la réservation", required = true, example = "CONFIRMEE")
            @PathVariable ReservationStatus statut
    ) {
        List<ReservationDto> reservations = reservationService.getReservationsByStatut(statut);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/user/{userId}/statut/{statut}")
    @Operation(
            summary = "👤📊 Réservations par utilisateur et statut",
            description = "Combine le filtrage par utilisateur et par statut de réservation"
    )
    @ApiResponse(responseCode = "200", description = "✅ Réservations filtrées récupérées")
    public ResponseEntity<List<ReservationDto>> getReservationsByUserIdAndStatut(
            @Parameter(description = "Identifiant de l'utilisateur", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Statut de la réservation", required = true, example = "CONFIRMEE")
            @PathVariable ReservationStatus statut
    ) {
        List<ReservationDto> reservations = reservationService.getReservationsByUserIdAndStatut(userId, statut);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/voyage/{voyageId}/statut/{statut}")
    @Operation(
            summary = "✈️📊 Réservations par voyage et statut",
            description = "Combine le filtrage par voyage et par statut de réservation"
    )
    @ApiResponse(responseCode = "200", description = "✅ Réservations filtrées récupérées")
    public ResponseEntity<List<ReservationDto>> getReservationsByVoyageIdAndStatut(
            @Parameter(description = "Identifiant du voyage", required = true, example = "1")
            @PathVariable Long voyageId,
            @Parameter(description = "Statut de la réservation", required = true, example = "CONFIRMEE")
            @PathVariable ReservationStatus statut
    ) {
        List<ReservationDto> reservations = reservationService.getReservationsByVoyageIdAndStatut(voyageId, statut);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/date-range")
    @Operation(
            summary = "📅 Réservations par plage de dates",
            description = "Récupère les réservations créées dans une période donnée"
    )
    @ApiResponse(responseCode = "200", description = "✅ Réservations dans la plage de dates")
    public ResponseEntity<List<ReservationDto>> getReservationsByDateRange(
            @Parameter(description = "Date de début (ISO 8601)", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Date de fin (ISO 8601)", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<ReservationDto> reservations = reservationService.getReservationsByDateRange(startDate, endDate);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/expired")
    @Operation(
            summary = "⏰ Réservations expirées",
            description = "Récupère toutes les réservations qui ont dépassé leur date d'expiration"
    )
    @ApiResponse(responseCode = "200", description = "✅ Réservations expirées récupérées")
    public ResponseEntity<List<ReservationDto>> getExpiredReservations() {
        List<ReservationDto> reservations = reservationService.getExpiredReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/voyage/{voyageId}/count-confirmed")
    @Operation(
            summary = "🔢 Compter les réservations confirmées",
            description = "Retourne le nombre total de réservations confirmées pour un voyage"
    )
    @ApiResponse(responseCode = "200", description = "✅ Nombre de réservations confirmées")
    public ResponseEntity<Long> countConfirmedReservationsByVoyageId(
            @Parameter(description = "Identifiant du voyage", required = true, example = "1")
            @PathVariable Long voyageId
    ) {
        Long count = reservationService.countConfirmedReservationsByVoyageId(voyageId);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    @Operation(
            summary = "➕ Créer une nouvelle réservation",
            description = "Crée une nouvelle réservation pour un voyage. Vérifie la disponibilité et les contraintes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "✅ Réservation créée avec succès"),
            @ApiResponse(responseCode = "400", description = "❌ Données de réservation invalides"),
            @ApiResponse(responseCode = "409", description = "⚠️ Conflit - Places non disponibles")
    })
    public ResponseEntity<?> createReservation(
            @Parameter(description = "Données de la nouvelle réservation", required = true)
            @Valid @RequestBody ReservationCreateDto reservationCreateDto
    ) {
        try {
            ReservationDto reservation = reservationService.createReservation(reservationCreateDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/statut")
    @Operation(
            summary = "🔄 Mettre à jour le statut",
            description = "Modifie le statut d'une réservation existante"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "✅ Statut mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "❌ Réservation non trouvée")
    })
    public ResponseEntity<ReservationDto> updateReservationStatut(
            @Parameter(description = "Identifiant de la réservation", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouveau statut", required = true, example = "CONFIRMEE")
            @RequestParam ReservationStatus statut
    ) {
        return reservationService.updateReservationStatut(id, statut)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/confirm")
    @Operation(
            summary = "✅ Confirmer une réservation",
            description = "Confirme une réservation en attente et met à jour son statut"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "✅ Réservation confirmée avec succès"),
            @ApiResponse(responseCode = "404", description = "❌ Réservation non trouvée")
    })
    public ResponseEntity<?> confirmReservation(
            @Parameter(description = "Identifiant de la réservation", required = true, example = "1")
            @PathVariable Long id
    ) {
        if (reservationService.confirmReservation(id)) {
            return ResponseEntity.ok("✅ Réservation confirmée avec succès");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Réservation non trouvée");
    }

    @PutMapping("/{id}/cancel")
    @Operation(
            summary = "❌ Annuler une réservation",
            description = "Annule une réservation et libère les places réservées"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "✅ Réservation annulée avec succès"),
            @ApiResponse(responseCode = "404", description = "❌ Réservation non trouvée")
    })
    public ResponseEntity<?> cancelReservation(
            @Parameter(description = "Identifiant de la réservation", required = true, example = "1")
            @PathVariable Long id
    ) {
        if (reservationService.cancelReservation(id)) {
            return ResponseEntity.ok("✅ Réservation annulée avec succès");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Réservation non trouvée");
    }

    @PutMapping("/{id}/expiration")
    @Operation(
            summary = "⏰ Définir date d'expiration",
            description = "Définit ou modifie la date d'expiration d'une réservation"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "✅ Date d'expiration mise à jour"),
            @ApiResponse(responseCode = "404", description = "❌ Réservation non trouvée")
    })
    public ResponseEntity<ReservationDto> setExpirationDate(
            @Parameter(description = "Identifiant de la réservation", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouvelle date d'expiration (ISO 8601)", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateExpiration
    ) {
        return reservationService.setExpirationDate(id, dateExpiration)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/process-expired")
    @Operation(
            summary = "🔄 Traiter réservations expirées",
            description = "Lance le processus de traitement automatique des réservations expirées"
    )
    @ApiResponse(responseCode = "200", description = "✅ Traitement des réservations expirées terminé")
    public ResponseEntity<String> processExpiredReservations() {
        reservationService.processExpiredReservations();
        return ResponseEntity.ok("✅ Réservations expirées traitées avec succès");
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "🗑️ Supprimer une réservation",
            description = "Supprime définitivement une réservation du système"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "✅ Réservation supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "❌ Réservation non trouvée")
    })
    public ResponseEntity<Void> deleteReservation(
            @Parameter(description = "Identifiant de la réservation", required = true, example = "1")
            @PathVariable Long id
    ) {
        if (reservationService.deleteReservation(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/paginated")
    @Operation(
            summary = "🔍 Obtenir toutes les réservations (paginated)",
            description = "Récupère la liste paginée de toutes les réservations"
    )
    @ApiResponse(responseCode = "200", description = "✅ Réservations paginées récupérées avec succès")
    public ResponseEntity<Page<ReservationDto>> getAllReservationsPaginated(
            @Parameter(description = "Numéro de page (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateReservation")
            @RequestParam(defaultValue = "dateReservation") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReservationDto> reservations = reservationService.getAllReservations(pageable);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/user/{userId}/paginated")
    @Operation(
            summary = "👤 Obtenir les réservations d'un utilisateur (paginated)",
            description = "Récupère les réservations paginées d'un utilisateur spécifique"
    )
    @ApiResponse(responseCode = "200", description = "✅ Réservations utilisateur paginées récupérées")
    public ResponseEntity<Page<ReservationDto>> getReservationsByUserIdPaginated(
            @Parameter(description = "Identifiant de l'utilisateur", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Numéro de page (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateReservation")
            @RequestParam(defaultValue = "dateReservation") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReservationDto> reservations = reservationService.getReservationsByUserId(userId, pageable);
        return ResponseEntity.ok(reservations);
    }


    @PostMapping("/search")
    @Operation(
            summary = "🔍 Recherche avancée de réservations",
            description = "Recherche de réservations avec filtres multiples et pagination"
    )
    @ApiResponse(responseCode = "200", description = "✅ Réservations filtrées récupérées")
    public ResponseEntity<Page<ReservationDto>> searchReservationsWithFilters(
            @Parameter(description = "Critères de filtrage", required = true)
            @RequestBody ReservationFilterDto filterDto) {

        Sort sort = Sort.by(Sort.Direction.fromString(filterDto.getSortDirection()), filterDto.getSortBy());
        Pageable pageable = PageRequest.of(filterDto.getPage(), filterDto.getSize(), sort);

        Page<ReservationDto> reservations = reservationService.searchReservationsWithFilters(filterDto, pageable);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/stats/total")
    @Operation(
            summary = "📊 Nombre total de réservations",
            description = "Retourne le nombre total de réservations dans le système"
    )
    @ApiResponse(responseCode = "200", description = "✅ Nombre total de réservations")
    public ResponseEntity<Long> getTotalReservations() {
        Long count = reservationService.getTotalReservations();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/statut/{statut}")
    @Operation(
            summary = "📊 Nombre de réservations par statut",
            description = "Retourne le nombre de réservations pour un statut donné"
    )
    @ApiResponse(responseCode = "200", description = "✅ Nombre de réservations par statut")
    public ResponseEntity<Long> getReservationsByStatutCount(
            @Parameter(description = "Statut de la réservation", required = true, example = "CONFIRMED")
            @PathVariable ReservationStatus statut) {
        Long count = reservationService.getReservationsByStatutCount(statut);
        return ResponseEntity.ok(count);
    }


    @GetMapping("/voyage/{voyageId}/paginated")
    @Operation(
            summary = "✈️ Obtenir les réservations d'un voyage (paginated)",
            description = "Récupère les réservations paginées d'un voyage spécifique"
    )
    @ApiResponse(responseCode = "200", description = "✅ Réservations voyage paginées récupérées")
    public ResponseEntity<Page<ReservationDto>> getReservationsByVoyageIdPaginated(
            @Parameter(description = "Identifiant du voyage", required = true, example = "1")
            @PathVariable Long voyageId,
            @Parameter(description = "Numéro de page (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateReservation")
            @RequestParam(defaultValue = "dateReservation") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReservationDto> reservations = reservationService.getReservationsByVoyageId(voyageId, pageable);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/statut/{statut}/paginated")
    @Operation(
            summary = "📊 Obtenir les réservations par statut (paginated)",
            description = "Récupère les réservations paginées filtrées par statut"
    )
    @ApiResponse(responseCode = "200", description = "✅ Réservations par statut paginées récupérées")
    public ResponseEntity<Page<ReservationDto>> getReservationsByStatutPaginated(
            @Parameter(description = "Statut de la réservation", required = true, example = "CONFIRMED")
            @PathVariable ReservationStatus statut,
            @Parameter(description = "Numéro de page (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateReservation")
            @RequestParam(defaultValue = "dateReservation") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReservationDto> reservations = reservationService.getReservationsByStatut(statut, pageable);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/user/{userId}/statut/{statut}/paginated")
    @Operation(
            summary = "👤📊 Réservations par utilisateur et statut (paginated)",
            description = "Récupère les réservations paginées filtrées par utilisateur et statut"
    )
    @ApiResponse(responseCode = "200", description = "✅ Réservations utilisateur/statut paginées récupérées")
    public ResponseEntity<Page<ReservationDto>> getReservationsByUserIdAndStatutPaginated(
            @Parameter(description = "Identifiant de l'utilisateur", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Statut de la réservation", required = true, example = "CONFIRMED")
            @PathVariable ReservationStatus statut,
            @Parameter(description = "Numéro de page (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateReservation")
            @RequestParam(defaultValue = "dateReservation") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReservationDto> reservations = reservationService.getReservationsByUserIdAndStatut(userId, statut, pageable);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/voyage/{voyageId}/statut/{statut}/paginated")
    @Operation(
            summary = "✈️📊 Réservations par voyage et statut (paginated)",
            description = "Récupère les réservations paginées filtrées par voyage et statut"
    )
    @ApiResponse(responseCode = "200", description = "✅ Réservations voyage/statut paginées récupérées")
    public ResponseEntity<Page<ReservationDto>> getReservationsByVoyageIdAndStatutPaginated(
            @Parameter(description = "Identifiant du voyage", required = true, example = "1")
            @PathVariable Long voyageId,
            @Parameter(description = "Statut de la réservation", required = true, example = "CONFIRMED")
            @PathVariable ReservationStatus statut,
            @Parameter(description = "Numéro de page (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateReservation")
            @RequestParam(defaultValue = "dateReservation") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReservationDto> reservations = reservationService.getReservationsByVoyageIdAndStatut(voyageId, statut, pageable);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/expired/paginated")
    @Operation(
            summary = "⏰ Réservations expirées (paginated)",
            description = "Récupère les réservations expirées de manière paginée"
    )
    @ApiResponse(responseCode = "200", description = "✅ Réservations expirées paginées récupérées")
    public ResponseEntity<Page<ReservationDto>> getExpiredReservationsPaginated(
            @Parameter(description = "Numéro de page (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateExpiration")
            @RequestParam(defaultValue = "dateExpiration") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReservationDto> reservations = reservationService.getExpiredReservations(pageable);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/date-range/paginated")
    @Operation(
            summary = "📅 Réservations par plage de dates (paginated)",
            description = "Récupère les réservations paginées créées dans une période donnée"
    )
    @ApiResponse(responseCode = "200", description = "✅ Réservations dans la plage de dates paginées")
    public ResponseEntity<Page<ReservationDto>> getReservationsByDateRangePaginated(
            @Parameter(description = "Date de début (ISO 8601)", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Date de fin (ISO 8601)", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Numéro de page (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateReservation")
            @RequestParam(defaultValue = "dateReservation") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReservationDto> reservations = reservationService.getReservationsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/stats/user/{userId}")
    @Operation(
            summary = "📊 Nombre de réservations par utilisateur",
            description = "Retourne le nombre de réservations pour un utilisateur donné"
    )
    @ApiResponse(responseCode = "200", description = "✅ Nombre de réservations par utilisateur")
    public ResponseEntity<Long> getReservationsByUserIdCount(
            @Parameter(description = "Identifiant de l'utilisateur", required = true, example = "1")
            @PathVariable Long userId) {
        Long count = reservationService.getReservationsByUserIdCount(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/voyage/{voyageId}")
    @Operation(
            summary = "📊 Nombre de réservations par voyage",
            description = "Retourne le nombre de réservations pour un voyage donné"
    )
    @ApiResponse(responseCode = "200", description = "✅ Nombre de réservations par voyage")
    public ResponseEntity<Long> getReservationsByVoyageIdCount(
            @Parameter(description = "Identifiant du voyage", required = true, example = "1")
            @PathVariable Long voyageId) {
        Long count = reservationService.getReservationsByVoyageIdCount(voyageId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/search/global")
    @Operation(
            summary = "Recherche globale de réservations",
            description = "Recherche dans tous les champs des réservations"
    )
    @ApiResponse(responseCode = "200", description = "Réservations trouvées")
    public ResponseEntity<Page<ReservationDto>> searchGlobal(
            @Parameter(description = "Terme de recherche", required = true) @RequestParam String query,
            @Parameter(description = "Numéro de page (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReservationDto> reservations = reservationService.searchGlobal(query, pageable);
        return ResponseEntity.ok(reservations);
    }
}