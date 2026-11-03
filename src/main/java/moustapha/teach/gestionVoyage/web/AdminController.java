package moustapha.teach.gestionVoyage.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moustapha.teach.gestionVoyage.domain.Role;
import moustapha.teach.gestionVoyage.domain.StatutVoyage;
import moustapha.teach.gestionVoyage.dto.*;
import moustapha.teach.gestionVoyage.security.SecurityUtils;
import moustapha.teach.gestionVoyage.service.AdminService;
import moustapha.teach.gestionVoyage.service.ControleService;
import moustapha.teach.gestionVoyage.service.ReservationService;
import moustapha.teach.gestionVoyage.service.VoyageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Administration")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final VoyageService voyageService;
    private final ReservationService reservationService;
    private final ControleService controleService;

    @Operation(summary = "Indicateurs du tableau de bord")
    @GetMapping("/statistiques")
    public StatsDto statistiques() {
        return adminService.statistiques();
    }

    @Operation(summary = "Tous les départs (plus récents d'abord)")
    @GetMapping("/voyages")
    public List<VoyageDto> voyages() {
        return voyageService.tous();
    }

    @Operation(summary = "Programmer un départ")
    @PostMapping("/voyages")
    @ResponseStatus(HttpStatus.CREATED)
    public VoyageDto creer(@Valid @RequestBody VoyageRequest req) {
        return voyageService.creer(req);
    }

    @Operation(summary = "Modifier un départ")
    @PutMapping("/voyages/{id}")
    public VoyageDto modifier(@PathVariable Long id, @Valid @RequestBody VoyageRequest req) {
        return voyageService.modifier(id, req);
    }

    @Operation(summary = "Changer le statut d'un départ (ANNULE annule aussi ses billets)")
    @PatchMapping("/voyages/{id}/statut")
    public VoyageDto statut(@PathVariable Long id, @RequestParam StatutVoyage statut) {
        return voyageService.changerStatut(id, statut);
    }

    @Operation(summary = "200 dernières réservations")
    @GetMapping("/reservations")
    public List<ReservationDto> reservations() {
        return reservationService.toutes();
    }

    @Operation(summary = "Comptes utilisateurs")
    @GetMapping("/utilisateurs")
    public List<UtilisateurDto> utilisateurs() {
        return adminService.utilisateurs();
    }

    @Operation(summary = "Créer un compte agent ou admin")
    @PostMapping("/utilisateurs")
    @ResponseStatus(HttpStatus.CREATED)
    public UtilisateurDto creerUtilisateur(@Valid @RequestBody CreerUtilisateurRequest req) {
        return adminService.creerUtilisateur(req);
    }

    @Operation(summary = "Activer, désactiver ou changer le rôle d'un compte")
    @PatchMapping("/utilisateurs/{id}")
    public UtilisateurDto modifierUtilisateur(@PathVariable Long id,
                                              @RequestParam(required = false) Boolean actif,
                                              @RequestParam(required = false) Role role) {
        return adminService.modifier(id, actif, role, SecurityUtils.courant().id());
    }

    @Operation(summary = "Journal des 200 derniers contrôles")
    @GetMapping("/controles")
    public List<ControleDto> controles() {
        return controleService.derniers();
    }
}
