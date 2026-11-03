package moustapha.teach.gestionVoyage.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UtilisateurConnecte courant() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UtilisateurConnecte u)) {
            throw new IllegalStateException("Aucun utilisateur connecté");
        }
        return u;
    }

    public static boolean estPersonnel() {
        String role = courant().role();
        return role.equals("AGENT") || role.equals("ADMIN");
    }
}
