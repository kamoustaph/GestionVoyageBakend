package moustapha.teach.gestionVoyage.mapper;

import moustapha.teach.gestionVoyage.dto.PaiementDto;
import moustapha.teach.gestionVoyage.dto.PaiementDetailDto;
import moustapha.teach.gestionVoyage.entities.Paiement;
import org.springframework.stereotype.Component;

@Component
public class PaiementMapper {

    public PaiementDto toDto(Paiement paiement) {
        if (paiement == null) return null;

        return new PaiementDto(
                paiement.getId(),
                paiement.getReservation() != null ? paiement.getReservation().getId() : null,
                paiement.getMontant(),
                paiement.getMethode(),
                paiement.getStatut(),
                paiement.getDatePaiement(),
                paiement.getUpdatedAt()
        );
    }

    public PaiementDetailDto toDetailDto(Paiement paiement) {
        if (paiement == null) return null;

        PaiementDetailDto dto = new PaiementDetailDto();
        dto.setId(paiement.getId());

        dto.setMontant(paiement.getMontant());
        dto.setMethode(paiement.getMethode());
        dto.setStatut(paiement.getStatut());
        dto.setDatePaiement(paiement.getDatePaiement());
        dto.setUpdatedAt(paiement.getUpdatedAt());

        if (paiement.getReservation() != null) {
            dto.setReservationId(paiement.getReservation().getId());

            if (paiement.getReservation().getUser() != null) {
                dto.setUserId(paiement.getReservation().getUser().getId());
                dto.setUserNom(paiement.getReservation().getUser().getUsername());
                dto.setUserPrenom(paiement.getReservation().getUser().getName());
            }

            if (paiement.getReservation().getVoyage() != null) {
                dto.setVoyageId(paiement.getReservation().getVoyage().getId());
                dto.setVoyageLieuDepart(paiement.getReservation().getVoyage().getLieuDepart());
                dto.setVoyageLieuArrivee(paiement.getReservation().getVoyage().getLieuArrivee());
            }
        }

        return dto;
    }
}