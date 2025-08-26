package moustapha.teach.gestionVoyage.services.impl;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import moustapha.teach.gestionVoyage.dto.UserRegistrationDTO;
import moustapha.teach.gestionVoyage.entities.Role;
import moustapha.teach.gestionVoyage.entities.User;
import moustapha.teach.gestionVoyage.entities.Validation;
import moustapha.teach.gestionVoyage.repositories.RoleRepository;
import moustapha.teach.gestionVoyage.repositories.UserRepository;
import moustapha.teach.gestionVoyage.enums.RoleType;
import moustapha.teach.gestionVoyage.services.UserService;
import moustapha.teach.gestionVoyage.services.ValidationService;
import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ValidationService validationService;

    public UserServiceImpl(UserRepository utilisateurRepository,
                           RoleRepository roleRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           ValidationService validationService) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
    }


    @Override
    public void inscription(UserRegistrationDTO registrationDTO) {
        if(!registrationDTO.getUsername().contains("@") || !registrationDTO.getUsername().contains(".")) {
            throw new RuntimeException("Votre email est invalide");
        }

        if(utilisateurRepository.findByUsername(registrationDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        if(registrationDTO.getRoleName() == null) {
            throw new RuntimeException("Le rôle est obligatoire");
        }

        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setName(registrationDTO.getName());
        user.setPhone(registrationDTO.getPhone());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setActif(false);

        RoleType roleEnum = registrationDTO.getRoleAsEnum();
        if (roleEnum == null) {
            throw new RuntimeException("Rôle invalide: " + registrationDTO.getRoleName());
        }

        Optional<Role> optionalRole = roleRepository.findByName(roleEnum);
        Role role = optionalRole.orElseGet(() -> {
            Role newRole = new Role(roleEnum);
            return roleRepository.save(newRole);
        });

        user.getRoles().add(role);

        User savedUser = utilisateurRepository.save(user);
        validationService.enregistrer(savedUser);

        log.info("Inscription réussie pour l'utilisateur: {}", registrationDTO.getUsername());
    }
    @Override
    public void activation(Map<String, String> activation) {
        String code = activation.get("code");
        Validation validation = validationService.lireEnFonctionDuCode(code);

        if(Instant.now().isAfter(validation.getExpiration())) {
            throw new RuntimeException("Votre code a expiré");
        }

        User utilisateur = utilisateurRepository.findById(validation.getUtilisateur().getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur inconnu"));

        utilisateur.setActif(true);
        utilisateurRepository.save(utilisateur);

        log.info("Compte activé avec succès pour l'utilisateur: {}", utilisateur.getUsername());
    }

    @Override
    public UserDetails loadUserByUsernameWithRoles(String username) throws UsernameNotFoundException {
        User user = utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Hibernate.initialize(user.getRoles());

        return user;
    }

    @Override
    public User getUserById(Long recipientUserId) {
        return utilisateurRepository.findById(recipientUserId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + recipientUserId));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur ne correspond à cet identifiant"));
    }



    @Override
    public void processOAuth2User(String email, String name, String pictureUrl) {
        Optional<User> existingUser = utilisateurRepository.findByUsername(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(name);
            user.setActif(true); // Activer automatiquement pour OAuth2
            utilisateurRepository.save(user);
            log.info("Utilisateur OAuth2 mis à jour: {}", email);
        } else {
            User newUser = new User();
            newUser.setUsername(email);
            newUser.setName(name);
            newUser.setActif(true);
            newUser.setPassword("OAUTH2_USER");

            RoleType roleEnum = RoleType.ROLE_USER;
            Optional<Role> optionalRole = roleRepository.findByName(roleEnum);
            Role role = optionalRole.orElseGet(() -> {
                Role newRole = new Role(roleEnum);
                return roleRepository.save(newRole);
            });

            newUser.getRoles().add(role);
            utilisateurRepository.save(newUser);
            log.info("Nouvel utilisateur OAuth2 créé: {}", email);
        }
    }
}