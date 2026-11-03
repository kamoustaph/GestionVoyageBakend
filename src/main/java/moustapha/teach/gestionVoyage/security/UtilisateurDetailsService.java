package moustapha.teach.gestionVoyage.security;

import lombok.RequiredArgsConstructor;
import moustapha.teach.gestionVoyage.repository.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilisateurDetailsService implements UserDetailsService {

    private final UtilisateurRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return repository.findByEmailIgnoreCase(email)
                .map(UtilisateurConnecte::of)
                .orElseThrow(() -> new UsernameNotFoundException("Compte introuvable"));
    }
}
