package moustapha.teach.gestionVoyage.services;


import moustapha.teach.gestionVoyage.entities.User;
import moustapha.teach.gestionVoyage.entities.Validation;

public interface ValidationService {
    void enregistrer(User utilisateur);
    Validation lireEnFonctionDuCode(String code);
}