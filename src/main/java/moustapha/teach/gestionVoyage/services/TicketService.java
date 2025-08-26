package moustapha.teach.gestionVoyage.services;

import moustapha.teach.gestionVoyage.dto.TicketDto;
import moustapha.teach.gestionVoyage.dto.TicketCreateDto;
import moustapha.teach.gestionVoyage.enums.StatutTicket;
import moustapha.teach.gestionVoyage.dto.TicketDetailDto;
import moustapha.teach.gestionVoyage.dto.TicketFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface TicketService {


    List<TicketDto> getAllTickets();

    Optional<TicketDto> getTicketById(Long id);

    Optional<TicketDto> getTicketByReservationId(Long reservationId);

    /**
     * Récupère un ticket par son code QR
     * @param qrCode Code QR du ticket
     * @return Optional contenant le ticket si trouvé
     */
    Optional<TicketDto> getTicketByQrCode(String qrCode);

    /**
     * Récupère tous les tickets par statut
     * @param statut Statut des tickets à récupérer
     * @return Liste des tickets avec le statut spécifié
     */
    List<TicketDto> getTicketsByStatut(StatutTicket statut);

    /**
     * Récupère tous les tickets d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des tickets de l'utilisateur
     */
    List<TicketDto> getTicketsByUserId(Long userId);

    /**
     * Récupère tous les tickets pour un voyage
     * @param voyageId ID du voyage
     * @return Liste des tickets pour le voyage
     */
    List<TicketDto> getTicketsByVoyageId(Long voyageId);

    /**
     * Crée un nouveau ticket
     * @param ticketCreateDto Données de création du ticket
     * @return Ticket créé
     * @throws RuntimeException si la réservation n'existe pas, n'est pas confirmée, 
     *                         ou si un ticket existe déjà pour cette réservation
     */
    TicketDto createTicket(TicketCreateDto ticketCreateDto);

    /**
     * Met à jour le statut d'un ticket
     * @param id ID du ticket
     * @param nouveauStatut Nouveau statut
     * @return Optional contenant le ticket mis à jour si trouvé
     */
    Optional<TicketDto> updateTicketStatut(Long id, StatutTicket nouveauStatut);

    /**
     * Supprime un ticket
     * @param id ID du ticket à supprimer
     * @return true si le ticket a été supprimé, false sinon
     */
    boolean deleteTicket(Long id);

    /**
     * Valide un ticket via son code QR
     * Change le statut du ticket de VALIDE à UTILISE
     * @param qrCode Code QR du ticket
     * @return true si le ticket a été validé avec succès, false sinon
     */
    boolean validateTicket(String qrCode);

    /**
     * Traite les tickets expirés
     * Met à jour le statut des tickets expirés vers EXPIRE
     */
    void processExpiredTickets();


    // Nouvelles méthodes avec pagination
    Page<TicketDto> getAllTickets(Pageable pageable);
    Page<TicketDto> getTicketsByStatut(StatutTicket statut, Pageable pageable);
    Page<TicketDto> getTicketsByReservationId(Long reservationId, Pageable pageable);
    Page<TicketDto> getTicketsByUserId(Long userId, Pageable pageable);
    Page<TicketDto> getTicketsByVoyageId(Long voyageId, Pageable pageable);
    Page<TicketDto> getTicketsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<TicketDto> getTicketsByQrCode(String qrCode, Pageable pageable);

    // Nouvelle méthode pour les filtres avancés
    Page<TicketDto> searchTicketsWithFilters(TicketFilterDto filterDto, Pageable pageable);

    // Nouvelle méthode pour les détails
    Optional<TicketDetailDto> getTicketDetail(Long id);

    // Méthodes de statistiques
    Long getTotalTickets();
    Long getTicketsByStatutCount(StatutTicket statut);
    Long getTicketsByUserIdCount(Long userId);
    Long getTicketsByVoyageIdCount(Long voyageId);



}