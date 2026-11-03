package moustapha.teach.gestionVoyage.security;

import moustapha.teach.gestionVoyage.domain.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/** Adaptateur entre l'entité Utilisateur et Spring Security. Autorité : ROLE_VOYAGEUR, ROLE_AGENT ou ROLE_ADMIN. */
public record UtilisateurConnecte(Long id, String email, String motDePasse, String role, boolean actif)
        implements UserDetails {

    public static UtilisateurConnecte of(Utilisateur u) {
        return new UtilisateurConnecte(u.getId(), u.getEmail(), u.getMotDePasse(), u.getRole().name(), u.isActif());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() { return motDePasse; }

    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isEnabled() { return actif; }
}
