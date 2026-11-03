package moustapha.teach.gestionVoyage.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moustapha.teach.gestionVoyage.dto.AuthResponse;
import moustapha.teach.gestionVoyage.dto.ConnexionRequest;
import moustapha.teach.gestionVoyage.dto.InscriptionRequest;
import moustapha.teach.gestionVoyage.dto.UtilisateurDto;
import moustapha.teach.gestionVoyage.security.SecurityUtils;
import moustapha.teach.gestionVoyage.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentification")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Créer un compte voyageur et recevoir un jeton")
    @PostMapping("/inscription")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse inscription(@Valid @RequestBody InscriptionRequest req) {
        return authService.inscrire(req);
    }

    @Operation(summary = "Se connecter et recevoir un jeton JWT")
    @PostMapping("/connexion")
    public AuthResponse connexion(@Valid @RequestBody ConnexionRequest req) {
        return authService.connecter(req);
    }

    @Operation(summary = "Profil de l'utilisateur connecté")
    @GetMapping("/moi")
    public UtilisateurDto moi() {
        return authService.moi(SecurityUtils.courant().id());
    }
}
