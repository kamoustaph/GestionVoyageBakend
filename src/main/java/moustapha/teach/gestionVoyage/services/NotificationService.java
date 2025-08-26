package moustapha.teach.gestionVoyage.services;

import moustapha.teach.gestionVoyage.entities.Validation;

public interface NotificationService {
    void sendValidationCode(Validation validation);

}
