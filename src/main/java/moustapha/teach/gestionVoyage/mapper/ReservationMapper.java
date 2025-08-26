package moustapha.teach.gestionVoyage.mapper;

import moustapha.teach.gestionVoyage.dto.ReservationDto;
import moustapha.teach.gestionVoyage.dto.ReservationDetailDto;
import moustapha.teach.gestionVoyage.entities.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationDto toDto(Reservation reservation) {
        if (reservation == null) return null;

        return new ReservationDto(
                reservation.getId(),
                reservation.getUser() != null ? reservation.getUser().getId() : null,
                reservation.getUser() != null ? reservation.getUser().getUsername() : null,
                reservation.getVoyage() != null ? reservation.getVoyage().getId() : null,
                reservation.getVoyage() != null ? reservation.getVoyage().getLieuDepart() : null,
                reservation.getVoyage() != null ? reservation.getVoyage().getLieuArrivee() : null,
                reservation.getStatut(),
                reservation.getDateReservation(),
                reservation.getDateExpiration()
        );
    }

    public ReservationDetailDto toDetailDto(Reservation reservation) {
        if (reservation == null) return null;

        ReservationDetailDto dto = new ReservationDetailDto();
        dto.setId(reservation.getId());

        // Informations utilisateur
        if (reservation.getUser() != null) {
            dto.setUserId(reservation.getUser().getId());
            dto.setUserNom(reservation.getUser().getUsername());
            dto.setUserPrenom(reservation.getUser().getName());
            dto.setUserEmail(reservation.getUser().getUsername());
        }

        // Informations voyage
        if (reservation.getVoyage() != null) {
            dto.setVoyageId(reservation.getVoyage().getId());
            dto.setVoyageLieuDepart(reservation.getVoyage().getLieuDepart());
            dto.setVoyageLieuArrivee(reservation.getVoyage().getLieuArrivee());
            dto.setVoyageDateDepart(reservation.getVoyage().getDateDepart());
            dto.setVoyageDateRetour(reservation.getVoyage().getDateRetour());
            dto.setVoyagePrix(reservation.getVoyage().getPrix() != null ?
                    reservation.getVoyage().getPrix().doubleValue() : 0.0);
        }

        // Informations réservation
        dto.setStatut(reservation.getStatut());
        dto.setDateReservation(reservation.getDateReservation());
        dto.setDateExpiration(reservation.getDateExpiration());
        dto.setNombrePlaces(reservation.getNombrePlaces());

        // Calcul du prix total
        if (reservation.getVoyage() != null && reservation.getVoyage().getPrix() != null &&
                reservation.getNombrePlaces() != null) {
            dto.setPrixTotal(reservation.getVoyage().getPrix().doubleValue() * reservation.getNombrePlaces());
        } else {
            dto.setPrixTotal(0.0);
        }

        return dto;
    }
}