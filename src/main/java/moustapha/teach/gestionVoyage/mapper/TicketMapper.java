package moustapha.teach.gestionVoyage.mapper;

import moustapha.teach.gestionVoyage.dto.TicketDto;
import moustapha.teach.gestionVoyage.dto.TicketDetailDto;
import moustapha.teach.gestionVoyage.entities.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketDto toDto(Ticket ticket) {
        if (ticket == null) return null;

        return new TicketDto(
                ticket.getId(),
                ticket.getReservation() != null ? ticket.getReservation().getId() : null,
                ticket.getQrCode(),
                ticket.getStatut(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }

    public TicketDetailDto toDetailDto(Ticket ticket) {
        if (ticket == null) return null;

        TicketDetailDto dto = new TicketDetailDto();
        dto.setId(ticket.getId());

        dto.setQrCode(ticket.getQrCode());
        dto.setStatut(ticket.getStatut());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());

        if (ticket.getReservation() != null) {
            dto.setReservationId(ticket.getReservation().getId());

            if (ticket.getReservation().getUser() != null) {
                dto.setUserId(ticket.getReservation().getUser().getId());
                dto.setUserNom(ticket.getReservation().getUser().getName());
                dto.setUserPrenom(ticket.getReservation().getUser().getUsername());
            }

            if (ticket.getReservation().getVoyage() != null) {
                dto.setVoyageId(ticket.getReservation().getVoyage().getId());
                dto.setVoyageLieuDepart(ticket.getReservation().getVoyage().getLieuDepart());
                dto.setVoyageLieuArrivee(ticket.getReservation().getVoyage().getLieuArrivee());
                dto.setVoyageDateDepart(ticket.getReservation().getVoyage().getDateDepart());
                dto.setVoyageDateRetour(ticket.getReservation().getVoyage().getDateRetour());
            }
        }
        return dto;
    }
}