package moustapha.teach.gestionVoyage.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import moustapha.teach.gestionVoyage.dto.*;
import moustapha.teach.gestionVoyage.enums.StatutTicket;
import moustapha.teach.gestionVoyage.services.TicketService;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*")
@Tag(name = "Tickets", description = "API de gestion des tickets")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    @Operation(summary = "Obtenir tous les tickets", description = "Retourne la liste de tous les tickets")
    @ApiResponse(responseCode = "200", description = "Liste des tickets récupérée avec succès")
    public ResponseEntity<List<TicketDto>> getAllTickets() {
        List<TicketDto> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un ticket par ID", description = "Retourne un ticket spécifique par son identifiant")
    @ApiResponse(responseCode = "200", description = "Ticket trouvé")
    @ApiResponse(responseCode = "404", description = "Ticket non trouvé")
    public ResponseEntity<TicketDto> getTicketById(
            @Parameter(description = "ID du ticket", required = true, example = "1") @PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Obtenir un ticket par ID de réservation", description = "Retourne le ticket associé à une réservation")
    @ApiResponse(responseCode = "200", description = "Ticket trouvé")
    @ApiResponse(responseCode = "404", description = "Ticket non trouvé")
    public ResponseEntity<TicketDto> getTicketByReservationId(
            @Parameter(description = "ID de la réservation", required = true, example = "1") @PathVariable Long reservationId) {
        return ticketService.getTicketByReservationId(reservationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/qr/{qrCode}")
    @Operation(summary = "Obtenir un ticket par code QR", description = "Retourne un ticket spécifique par son code QR")
    @ApiResponse(responseCode = "200", description = "Ticket trouvé")
    @ApiResponse(responseCode = "404", description = "Ticket non trouvé")
    public ResponseEntity<TicketDto> getTicketByQrCode(
            @Parameter(description = "Code QR du ticket", required = true, example = "QR123456789") @PathVariable String qrCode) {
        return ticketService.getTicketByQrCode(qrCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Obtenir les tickets par statut", description = "Retourne les tickets filtrés par statut")
    @ApiResponse(responseCode = "200", description = "Tickets récupérés avec succès")
    public ResponseEntity<List<TicketDto>> getTicketsByStatut(
            @Parameter(description = "Statut du ticket", required = true, example = "VALIDE") @PathVariable StatutTicket statut) {
        List<TicketDto> tickets = ticketService.getTicketsByStatut(statut);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtenir les tickets par utilisateur", description = "Retourne les tickets d'un utilisateur spécifique")
    @ApiResponse(responseCode = "200", description = "Tickets récupérés avec succès")
    public ResponseEntity<List<TicketDto>> getTicketsByUserId(
            @Parameter(description = "ID de l'utilisateur", required = true, example = "1") @PathVariable Long userId) {
        List<TicketDto> tickets = ticketService.getTicketsByUserId(userId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/voyage/{voyageId}")
    @Operation(summary = "Obtenir les tickets par voyage", description = "Retourne les tickets associés à un voyage spécifique")
    @ApiResponse(responseCode = "200", description = "Tickets récupérés avec succès")
    public ResponseEntity<List<TicketDto>> getTicketsByVoyageId(
            @Parameter(description = "ID du voyage", required = true, example = "1") @PathVariable Long voyageId) {
        List<TicketDto> tickets = ticketService.getTicketsByVoyageId(voyageId);
        return ResponseEntity.ok(tickets);
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau ticket", description = "Crée un nouveau ticket pour une réservation")
    @ApiResponse(responseCode = "201", description = "Ticket créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> createTicket(
            @Parameter(description = "Données du ticket à créer", required = true) @Valid @RequestBody TicketCreateDto ticketCreateDto) {
        try {
            TicketDto ticket = ticketService.createTicket(ticketCreateDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/statut")
    @Operation(summary = "Mettre à jour le statut d'un ticket", description = "Modifie le statut d'un ticket existant")
    @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Ticket non trouvé")
    public ResponseEntity<TicketDto> updateTicketStatut(
            @Parameter(description = "ID du ticket", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Nouveau statut", required = true, example = "UTILISE") @RequestParam StatutTicket statut) {
        return ticketService.updateTicketStatut(id, statut)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/validate/{qrCode}")
    @Operation(summary = "Valider un ticket", description = "Valide un ticket par son code QR")
    @ApiResponse(responseCode = "200", description = "Ticket validé avec succès")
    @ApiResponse(responseCode = "400", description = "Impossible de valider le ticket")
    public ResponseEntity<?> validateTicket(
            @Parameter(description = "Code QR du ticket", required = true, example = "QR123456789") @PathVariable String qrCode) {
        if (ticketService.validateTicket(qrCode)) {
            return ResponseEntity.ok().body("Ticket validé avec succès");
        }
        return ResponseEntity.badRequest().body("Impossible de valider le ticket (déjà utilisé, expiré ou inexistant)");
    }

    @PostMapping("/process-expired")
    @Operation(summary = "Traiter les tickets expirés", description = "Traite automatiquement tous les tickets expirés")
    @ApiResponse(responseCode = "200", description = "Tickets expirés traités avec succès")
    public ResponseEntity<String> processExpiredTickets() {
        ticketService.processExpiredTickets();
        return ResponseEntity.ok("Tickets expirés traités avec succès");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un ticket", description = "Supprime un ticket existant")
    @ApiResponse(responseCode = "204", description = "Ticket supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Ticket non trouvé")
    public ResponseEntity<Void> deleteTicket(
            @Parameter(description = "ID du ticket", required = true, example = "1") @PathVariable Long id) {
        if (ticketService.deleteTicket(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/paginated")
    @Operation(summary = "Obtenir tous les tickets (paginated)", description = "Retourne la liste paginée de tous les tickets")
    @ApiResponse(responseCode = "200", description = "Tickets paginés récupérés avec succès")
    public ResponseEntity<Page<TicketDto>> getAllTicketsPaginated(
            @Parameter(description = "Numéro de page (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc") @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TicketDto> tickets = ticketService.getAllTickets(pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/statut/{statut}/paginated")
    @Operation(summary = "Obtenir les tickets par statut (paginated)", description = "Retourne les tickets paginés filtrés par statut")
    @ApiResponse(responseCode = "200", description = "Tickets par statut paginés récupérés avec succès")
    public ResponseEntity<Page<TicketDto>> getTicketsByStatutPaginated(
            @Parameter(description = "Statut du ticket", required = true, example = "VALIDE") @PathVariable StatutTicket statut,
            @Parameter(description = "Numéro de page (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc") @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TicketDto> tickets = ticketService.getTicketsByStatut(statut, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/reservation/{reservationId}/paginated")
    @Operation(summary = "Obtenir les tickets par réservation (paginated)", description = "Retourne les tickets paginés d'une réservation")
    @ApiResponse(responseCode = "200", description = "Tickets par réservation paginés récupérés avec succès")
    public ResponseEntity<Page<TicketDto>> getTicketsByReservationIdPaginated(
            @Parameter(description = "ID de la réservation", required = true, example = "1") @PathVariable Long reservationId,
            @Parameter(description = "Numéro de page (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc") @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TicketDto> tickets = ticketService.getTicketsByReservationId(reservationId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/user/{userId}/paginated")
    @Operation(summary = "Obtenir les tickets par utilisateur (paginated)", description = "Retourne les tickets paginés d'un utilisateur")
    @ApiResponse(responseCode = "200", description = "Tickets par utilisateur paginés récupérés avec succès")
    public ResponseEntity<Page<TicketDto>> getTicketsByUserIdPaginated(
            @Parameter(description = "ID de l'utilisateur", required = true, example = "1") @PathVariable Long userId,
            @Parameter(description = "Numéro de page (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc") @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TicketDto> tickets = ticketService.getTicketsByUserId(userId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/voyage/{voyageId}/paginated")
    @Operation(summary = "Obtenir les tickets par voyage (paginated)", description = "Retourne les tickets paginés d'un voyage")
    @ApiResponse(responseCode = "200", description = "Tickets par voyage paginés récupérés avec succès")
    public ResponseEntity<Page<TicketDto>> getTicketsByVoyageIdPaginated(
            @Parameter(description = "ID du voyage", required = true, example = "1") @PathVariable Long voyageId,
            @Parameter(description = "Numéro de page (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc") @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TicketDto> tickets = ticketService.getTicketsByVoyageId(voyageId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/date-range/paginated")
    @Operation(summary = "Obtenir les tickets par plage de dates (paginated)", description = "Retourne les tickets paginés dans une période")
    @ApiResponse(responseCode = "200", description = "Tickets par date paginés récupérés avec succès")
    public ResponseEntity<Page<TicketDto>> getTicketsByDateRangePaginated(
            @Parameter(description = "Date de début", required = true, example = "2024-01-01T00:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Date de fin", required = true, example = "2024-12-31T23:59:59") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Numéro de page (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direction de tri", example = "desc") @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TicketDto> tickets = ticketService.getTicketsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/qr/paginated")
    @Operation(summary = "Rechercher les tickets par code QR (paginated)", description = "Retourne les tickets paginés contenant le code QR")
    @ApiResponse(responseCode = "200", description = "Tickets par QR code paginés récupérés avec succès")
    public ResponseEntity<Page<TicketDto>> getTicketsByQrCodePaginated(
            @Parameter(description = "Code QR ou partie du code", required = true, example = "TKT-") @RequestParam String qrCode,
            @Parameter(description = "Numéro de page (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "qrCode") @RequestParam(defaultValue = "qrCode") String sortBy,
            @Parameter(description = "Direction de tri", example = "asc") @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TicketDto> tickets = ticketService.getTicketsByQrCode(qrCode, pageable);
        return ResponseEntity.ok(tickets);
    }

    @PostMapping("/search")
    @Operation(summary = "Recherche avancée de tickets", description = "Recherche de tickets avec filtres multiples et pagination")
    @ApiResponse(responseCode = "200", description = "Tickets filtrés récupérés avec succès")
    public ResponseEntity<Page<TicketDto>> searchTicketsWithFilters(
            @Parameter(description = "Critères de filtrage", required = true) @RequestBody TicketFilterDto filterDto) {

        Sort sort = Sort.by(Sort.Direction.fromString(filterDto.getSortDirection()), filterDto.getSortBy());
        Pageable pageable = PageRequest.of(filterDto.getPage(), filterDto.getSize(), sort);

        Page<TicketDto> tickets = ticketService.searchTicketsWithFilters(filterDto, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}/detail")
    @Operation(summary = "Obtenir les détails d'un ticket", description = "Retourne les détails complets d'un ticket")
    @ApiResponse(responseCode = "200", description = "Détails du ticket récupérés avec succès")
    @ApiResponse(responseCode = "404", description = "Ticket non trouvé")
    public ResponseEntity<TicketDetailDto> getTicketDetail(
            @Parameter(description = "ID du ticket", required = true, example = "1") @PathVariable Long id) {
        return ticketService.getTicketDetail(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ENDPOINTS DE STATISTIQUES

    @GetMapping("/stats/total")
    @Operation(summary = "Nombre total de tickets", description = "Retourne le nombre total de tickets")
    @ApiResponse(responseCode = "200", description = "Nombre total de tickets")
    public ResponseEntity<Long> getTotalTickets() {
        Long count = ticketService.getTotalTickets();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/statut/{statut}")
    @Operation(summary = "Nombre de tickets par statut", description = "Retourne le nombre de tickets pour un statut donné")
    @ApiResponse(responseCode = "200", description = "Nombre de tickets par statut")
    public ResponseEntity<Long> getTicketsByStatutCount(
            @Parameter(description = "Statut du ticket", required = true, example = "VALIDE") @PathVariable StatutTicket statut) {
        Long count = ticketService.getTicketsByStatutCount(statut);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/user/{userId}")
    @Operation(summary = "Nombre de tickets par utilisateur", description = "Retourne le nombre de tickets pour un utilisateur donné")
    @ApiResponse(responseCode = "200", description = "Nombre de tickets par utilisateur")
    public ResponseEntity<Long> getTicketsByUserIdCount(
            @Parameter(description = "ID de l'utilisateur", required = true, example = "1") @PathVariable Long userId) {
        Long count = ticketService.getTicketsByUserIdCount(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/voyage/{voyageId}")
    @Operation(summary = "Nombre de tickets par voyage", description = "Retourne le nombre de tickets pour un voyage donné")
    @ApiResponse(responseCode = "200", description = "Nombre de tickets par voyage")
    public ResponseEntity<Long> getTicketsByVoyageIdCount(
            @Parameter(description = "ID du voyage", required = true, example = "1") @PathVariable Long voyageId) {
        Long count = ticketService.getTicketsByVoyageIdCount(voyageId);
        return ResponseEntity.ok(count);
    }
}