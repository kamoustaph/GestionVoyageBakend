package moustapha.teach.gestionVoyage.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moustapha.teach.gestionVoyage.dto.BilletDto;
import moustapha.teach.gestionVoyage.dto.PaiementRequest;
import moustapha.teach.gestionVoyage.dto.ReservationDto;
import moustapha.teach.gestionVoyage.dto.ReservationRequest;
import moustapha.teach.gestionVoyage.security.SecurityUtils;
import moustapha.teach.gestionVoyage.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Réservations et billets (voyageur)")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "Réserver des places (bloquées pendant le délai de paiement)")
    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationDto reserver(@Valid @RequestBody ReservationRequest req) {
        return reservationService.reserver(req, SecurityUtils.courant().id());
    }

    @Operation(summary = "Payer une réservation : émet les billets avec QR code")
    @PostMapping("/reservations/{id}/paiement")
    public ReservationDto payer(@PathVariable Long id, @Valid @RequestBody PaiementRequest req) {
        return reservationService.payer(id, req, SecurityUtils.courant());
    }

    @Operation(summary = "Annuler une réservation et libérer les places")
    @PostMapping("/reservations/{id}/annulation")
    public ReservationDto annuler(@PathVariable Long id) {
        return reservationService.annuler(id, SecurityUtils.courant());
    }

    @Operation(summary = "Mes réservations")
    @GetMapping("/reservations/miennes")
    public List<ReservationDto> miennes() {
        return reservationService.mesReservations(SecurityUtils.courant().id());
    }

    @Operation(summary = "Détail d'une de mes réservations")
    @GetMapping("/reservations/{id}")
    public ReservationDto detail(@PathVariable Long id) {
        return reservationService.detail(id, SecurityUtils.courant());
    }

    @Operation(summary = "Mes billets (avec contenu du QR code pour les billets valides)")
    @GetMapping("/billets/miens")
    public List<BilletDto> mesBillets() {
        return reservationService.mesBillets(SecurityUtils.courant().id());
    }

    @Operation(summary = "Un billet par son code")
    @GetMapping("/billets/{code}")
    public BilletDto billet(@PathVariable String code) {
        return reservationService.billet(code, SecurityUtils.courant());
    }
}
