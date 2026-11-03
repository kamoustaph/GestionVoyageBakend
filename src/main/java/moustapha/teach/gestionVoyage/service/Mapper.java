package moustapha.teach.gestionVoyage.service;

import lombok.RequiredArgsConstructor;
import moustapha.teach.gestionVoyage.domain.Billet;
import moustapha.teach.gestionVoyage.domain.Controle;
import moustapha.teach.gestionVoyage.domain.Paiement;
import moustapha.teach.gestionVoyage.domain.Reservation;
import moustapha.teach.gestionVoyage.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

/** Conversion entités vers DTO (le QR n'est inclus que pour le propriétaire ou le personnel). */
@Component
@RequiredArgsConstructor
public class Mapper {

    private final QrSigner qrSigner;

    public BilletDto billet(Billet b, boolean avecQr) {
        return new BilletDto(b.getId(), b.getCode(), b.getNomPassager(), b.getSiege(), b.getStatut(),
                b.getReservation().getReference(), VoyageDto.of(b.getVoyage()),
                avecQr ? qrSigner.contenu(b) : null, b.getEmbarqueLe());
    }

    public ReservationDto reservation(Reservation r, boolean avecBillets) {
        Paiement p = r.getPaiement();
        PaiementDto paiement = p == null ? null
                : new PaiementDto(p.getMontant(), p.getMethode(), p.getTelephone(), p.getTransactionRef(), p.getPayeLe());
        List<BilletDto> billets = avecBillets
                ? r.getBillets().stream().map(b -> billet(b, true)).toList()
                : List.of();
        return new ReservationDto(r.getId(), r.getReference(), r.getStatut(), r.getNombrePlaces(), r.getMontantTotal(),
                r.getCreeLe(), r.getExpireLe(), VoyageDto.of(r.getVoyage()),
                r.getUtilisateur().getNomComplet(), r.getUtilisateur().getEmail(), paiement, billets);
    }

    public ControleDto controle(Controle c) {
        Billet b = c.getBillet();
        return new ControleDto(c.getId(), c.getScanneLe(), c.getResultat(), c.getResultat().getLibelle(),
                c.getAgent().getNomComplet(),
                b == null ? null : b.getCode(),
                b == null ? null : b.getNomPassager(),
                b == null ? null : b.getVoyage().getVilleDepart() + " - " + b.getVoyage().getVilleArrivee());
    }
}
