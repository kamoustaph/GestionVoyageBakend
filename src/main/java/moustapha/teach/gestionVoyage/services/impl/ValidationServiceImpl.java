package moustapha.teach.gestionVoyage.services.impl;


import moustapha.teach.gestionVoyage.entities.User;
import moustapha.teach.gestionVoyage.entities.Validation;
import moustapha.teach.gestionVoyage.repositories.ValidationRepository;
import moustapha.teach.gestionVoyage.services.NotificationService;
import moustapha.teach.gestionVoyage.services.ValidationService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class ValidationServiceImpl implements ValidationService {

    private final ValidationRepository validationRepository;
    private final NotificationService notificationService;

    public ValidationServiceImpl(ValidationRepository validationRepository,
                                 NotificationService notificationService) {
        this.validationRepository = validationRepository;
        this.notificationService = notificationService;
    }
    @Override
    public void enregistrer(User utilisateur) {
        Validation validation = new Validation();
        validation.setUtilisateur(utilisateur);
        Instant creation = Instant.now();
        validation.setCreation(creation);
        Instant expiration = creation.plus(10, MINUTES);
        validation.setExpiration(expiration);
        Random random = new Random();
        int randomInteger = random.nextInt(999999);
        String code = String.format("%06d", randomInteger);

        validation.setCode(code);
        this.validationRepository.save(validation);
        this.notificationService.sendValidationCode(validation);
    }
    @Override
    public Validation lireEnFonctionDuCode(String code) {
        return this.validationRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Votre code est invalide"));
    }
}
