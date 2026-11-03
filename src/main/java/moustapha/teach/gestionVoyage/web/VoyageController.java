package moustapha.teach.gestionVoyage.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import moustapha.teach.gestionVoyage.dto.VoyageDto;
import moustapha.teach.gestionVoyage.service.VoyageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Voyages (public)")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VoyageController {

    private final VoyageService voyageService;

    @Operation(summary = "Villes desservies")
    @GetMapping("/villes")
    public List<String> villes() {
        return VoyageService.VILLES;
    }

    @Operation(summary = "Rechercher des départs (tous paramètres optionnels)")
    @GetMapping("/voyages")
    public List<VoyageDto> rechercher(@RequestParam(required = false) String depart,
                                      @RequestParam(required = false) String arrivee,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return voyageService.rechercher(depart, arrivee, date);
    }

    @Operation(summary = "Prochains départs, pour le tableau d'affichage de la page d'accueil")
    @GetMapping("/voyages/prochains")
    public List<VoyageDto> prochains(@RequestParam(defaultValue = "8") int limite) {
        return voyageService.prochainsDeparts(limite);
    }

    @Operation(summary = "Détail d'un départ")
    @GetMapping("/voyages/{id}")
    public VoyageDto detail(@PathVariable Long id) {
        return voyageService.detail(id);
    }
}
