package moustapha.teach.gestionVoyage.controllers;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moustapha.teach.gestionVoyage.dto.AuthentificationDTO;
import moustapha.teach.gestionVoyage.dto.UserRegistrationDTO;
import moustapha.teach.gestionVoyage.securities.JwtService;
import moustapha.teach.gestionVoyage.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurControleur {
    private static final Logger logger = LoggerFactory.getLogger(UtilisateurControleur.class);

    private final AuthenticationManager authenticationManager;
    private final UserService utilisateurService;
    private final JwtService jwtService;

    @PostMapping(value = "/inscription", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> inscription(@RequestBody UserRegistrationDTO registrationDTO) {
        logger.info("Tentative d'inscription pour l'utilisateur: {}", registrationDTO.getUsername());
        Map<String, Object> response = new HashMap<>();

        try {
            utilisateurService.inscription(registrationDTO);
            logger.info("Inscription réussie pour: {}", registrationDTO.getUsername());

            response.put("success", true);
            response.put("message", "Inscription réussie. Un email d'activation a été envoyé.");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.warn("Échec inscription - Données invalides: {} - Erreur: {}",
                    registrationDTO.getUsername(), e.getMessage());

            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            logger.error("Échec inscription - Erreur serveur pour: {} - Erreur: {}",
                    registrationDTO.getUsername(), e.getMessage(), e);

            response.put("success", false);
            response.put("message", "Une erreur interne est survenue lors de l'inscription");
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping(value = "/activation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> activation(@RequestBody Map<String, String> activation) {
        String code = activation.get("code");
        logger.info("Tentative d'activation avec code: {}", code);
        Map<String, Object> response = new HashMap<>();

        try {
            utilisateurService.activation(activation);
            logger.info("Activation réussie pour le code: {}", code);

            response.put("success", true);
            response.put("message", "Compte activé avec succès. Vous pouvez maintenant vous connecter.");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.warn("Échec activation - Code invalide: {} - Erreur: {}", code, e.getMessage());

            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            logger.error("Échec activation - Erreur serveur pour le code: {} - Erreur: {}",
                    code, e.getMessage(), e);

            response.put("success", false);
            response.put("message", "Une erreur interne est survenue lors de l'activation");
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping(value = "/connexion", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> connexion(@RequestBody AuthentificationDTO authentificationDTO) {
        logger.info("Tentative de connexion pour: {}", authentificationDTO.username());
        Map<String, Object> response = new HashMap<>();

        try {
            final Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authentificationDTO.username(),
                            authentificationDTO.password()
                    )
            );

            if (authenticate.isAuthenticated()) {
                logger.info("Connexion réussie pour: {}", authentificationDTO.username());
                String token = jwtService.generate(authentificationDTO.username()); // Retrait du .toString()

                response.put("success", true);
                response.put("token", token);
                response.put("message", "Connexion réussie");
                response.put("status", HttpStatus.OK.value());
                return ResponseEntity.ok(response);
            }

            logger.warn("Échec connexion - Authentification échouée pour: {}", authentificationDTO.username());
            response.put("success", false);
            response.put("message", "Identifiants incorrects");
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } catch (BadCredentialsException e) {
            logger.warn("Identifiants invalides pour: {}", authentificationDTO.username());
            response.put("success", false);
            response.put("message", "Identifiants incorrects");
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } catch (DisabledException e) {
            logger.warn("Compte désactivé pour: {}", authentificationDTO.username());
            response.put("success", false);
            response.put("message", "Compte non activé. Veuillez vérifier votre email.");
            response.put("status", HttpStatus.FORBIDDEN.value());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (Exception e) {
            logger.error("Erreur serveur lors de la connexion pour: {} - Erreur: {}",
                    authentificationDTO.username(), e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Une erreur interne est survenue");
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}