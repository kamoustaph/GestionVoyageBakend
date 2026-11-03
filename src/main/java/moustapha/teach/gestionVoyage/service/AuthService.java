package moustapha.teach.gestionVoyage.service;

import lombok.RequiredArgsConstructor;
import moustapha.teach.gestionVoyage.domain.Role;
import moustapha.teach.gestionVoyage.domain.Utilisateur;
import moustapha.teach.gestionVoyage.dto.AuthResponse;
import moustapha.teach.gestionVoyage.dto.ConnexionRequest;
import moustapha.teach.gestionVoyage.dto.InscriptionRequest;
import moustapha.teach.gestionVoyage.dto.UtilisateurDto;
import moustapha.teach.gestionVoyage.repository.UtilisateurRepository;
import moustapha.teach.gestionVoyage.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /** Inscription publique : toujours avec le rôle VOYAGEUR. Les agents sont créés par un admin. */
    @Transactional
    public AuthResponse inscrire(InscriptionRequest req) {
        String email = req.email().trim().toLowerCase();
        if (repository.existsByEmailIgnoreCase(email)) {
            throw ApiException.conflit("Un compte existe déjà avec cet email");
        }
        Utilisateur u = repository.save(new Utilisateur(email, req.nomComplet().trim(),
                normaliserTelephone(req.telephone()), passwordEncoder.encode(req.motDePasse()), Role.VOYAGEUR));
        return reponse(u);
    }

    @Transactional(readOnly = true)
    public AuthResponse connecter(ConnexionRequest req) {
        Utilisateur u = repository.findByEmailIgnoreCase(req.email().trim())
                .filter(x -> passwordEncoder.matches(req.motDePasse(), x.getMotDePasse()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect"));
        if (!u.isActif()) {
            throw ApiException.interdit("Ce compte est désactivé. Contactez l'administrateur.");
        }
        return reponse(u);
    }

    @Transactional(readOnly = true)
    public UtilisateurDto moi(Long id) {
        return repository.findById(id).map(UtilisateurDto::of)
                .orElseThrow(() -> ApiException.introuvable("Compte"));
    }

    private AuthResponse reponse(Utilisateur u) {
        return new AuthResponse(jwtService.generer(u), jwtService.getExpirationSecondes(), UtilisateurDto.of(u));
    }

    static String normaliserTelephone(String tel) {
        if (tel == null || tel.isBlank()) {
            return null;
        }
        String chiffres = tel.replaceAll("[^0-9+]", "");
        return chiffres.startsWith("+") ? chiffres : "+221" + chiffres;
    }
}
