package moustapha.teach.gestionVoyage.services;


import jakarta.validation.constraints.NotNull;
import moustapha.teach.gestionVoyage.dto.UserRegistrationDTO;
import moustapha.teach.gestionVoyage.entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

public interface UserService extends UserDetailsService {
    void inscription(UserRegistrationDTO utilisateur);
    void activation(Map<String, String> activation);
    public UserDetails loadUserByUsernameWithRoles(String username);
    User getUserById(@NotNull(message = "Au moins un destinataire est requis") Long recipientUserId);
    void processOAuth2User(String email, String name, String pictureUrl);
}