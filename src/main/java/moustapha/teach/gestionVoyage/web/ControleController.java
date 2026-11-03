package moustapha.teach.gestionVoyage.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moustapha.teach.gestionVoyage.dto.*;
import moustapha.teach.gestionVoyage.security.SecurityUtils;
import moustapha.teach.gestionVoyage.service.ControleService;
import moustapha.teach.gestionVoyage.service.VoyageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Contrôle des billets (agent)")
@RestController
@RequestMapping("/api/controle")
@RequiredArgsConstructor
public class ControleController {

    private final ControleService controleService;
    private final VoyageService voyageService;

    @Operation(summary = "Vérifier un QR code sans le consommer (affiche le passager)")
    @PostMapping("/verification")
    public ControleResultatDto verifier(@Valid @RequestBody ControleRequest req) {
        return controleService.verifier(req, SecurityUtils.courant().id());
    }

    @Operation(summary = "Confirmer l'embarquement : le billet passe à UTILISE")
    @PostMapping("/embarquement")
    public ControleResultatDto embarquer(@Valid @RequestBody ControleRequest req) {
        return controleService.embarquer(req, SecurityUtils.courant().id());
    }

    @Operation(summary = "Mes scans du jour")
    @GetMapping("/historique")
    public List<ControleDto> historique() {
        return controleService.historiqueDuJour(SecurityUtils.courant().id());
    }

    @Operation(summary = "Départs d'aujourd'hui et de demain à contrôler")
    @GetMapping("/departs")
    public List<VoyageDto> departs() {
        return voyageService.departsAControler();
    }

    @Operation(summary = "Liste d'embarquement d'un départ")
    @GetMapping("/departs/{voyageId}/passagers")
    public List<PassagerManifesteDto> manifeste(@PathVariable Long voyageId) {
        return voyageService.manifeste(voyageId);
    }
}
