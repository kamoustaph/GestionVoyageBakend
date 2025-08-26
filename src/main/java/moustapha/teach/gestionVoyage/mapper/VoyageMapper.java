package moustapha.teach.gestionVoyage.mapper;

import moustapha.teach.gestionVoyage.dto.VoyageDto;
import moustapha.teach.gestionVoyage.dto.VoyageCreateDto;
import moustapha.teach.gestionVoyage.entities.Voyage;
import org.springframework.stereotype.Component;

@Component
public class VoyageMapper {

    public VoyageDto toDto(Voyage voyage) {
        if (voyage == null) return null;

        return new VoyageDto(
                voyage.getId(),
                voyage.getLieuDepart(),
                voyage.getLieuArrivee(),
                voyage.getDateDepart(),
                voyage.getDateRetour(),
                voyage.getPrix(),
                voyage.getPlacesDisponibles(),
                voyage.getPlacesTotal(),
                voyage.getStatut()
        );
    }

    public Voyage toEntity(VoyageCreateDto voyageCreateDto) {
        if (voyageCreateDto == null) return null;

        Voyage voyage = new Voyage();
        voyage.setLieuDepart(voyageCreateDto.getLieuDepart());
        voyage.setLieuArrivee(voyageCreateDto.getLieuArrivee());
        voyage.setDateDepart(voyageCreateDto.getDateDepart());
        voyage.setDateRetour(voyageCreateDto.getDateRetour());
        voyage.setPrix(voyageCreateDto.getPrix());
        voyage.setPlacesDisponibles(voyageCreateDto.getPlacesDisponibles());
        voyage.setPlacesTotal(voyageCreateDto.getPlacesTotal());
        voyage.setStatut(voyageCreateDto.getStatut());
        return voyage;
    }

    public void updateEntity(VoyageCreateDto dto, Voyage voyage) {
        if (dto == null || voyage == null) return;

        voyage.setLieuDepart(dto.getLieuDepart());
        voyage.setLieuArrivee(dto.getLieuArrivee());
        voyage.setDateDepart(dto.getDateDepart());
        voyage.setDateRetour(dto.getDateRetour());
        voyage.setPrix(dto.getPrix());
        voyage.setPlacesDisponibles(dto.getPlacesDisponibles());
        voyage.setPlacesTotal(dto.getPlacesTotal());
        voyage.setStatut(dto.getStatut());
    }
}
