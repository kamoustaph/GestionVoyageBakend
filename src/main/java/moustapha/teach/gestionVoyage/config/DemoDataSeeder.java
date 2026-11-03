package moustapha.teach.gestionVoyage.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moustapha.teach.gestionVoyage.domain.*;
import moustapha.teach.gestionVoyage.dto.PaiementRequest;
import moustapha.teach.gestionVoyage.dto.PassagerRequest;
import moustapha.teach.gestionVoyage.dto.ReservationRequest;
import moustapha.teach.gestionVoyage.repository.UtilisateurRepository;
import moustapha.teach.gestionVoyage.repository.VoyageRepository;
import moustapha.teach.gestionVoyage.security.UtilisateurConnecte;
import moustapha.teach.gestionVoyage.service.ReservationService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Données de démonstration (app.demo.enabled=true) : 3 comptes, 14 jours de départs sur les grands axes,
 * et une réservation payée pour le voyageur sur un départ du jour, prête à être scannée.
 * Ne s'exécute que si la base ne contient aucun utilisateur.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.demo", name = "enabled", havingValue = "true")
public class DemoDataSeeder implements ApplicationRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final VoyageRepository voyageRepository;
    private final ReservationService reservationService;
    private final PasswordEncoder passwordEncoder;

    /** Trajet : départ, arrivée, gare départ, gare arrivée, durée (min), prix standard (FCFA), heures de départ. */
    private record Ligne(String de, String a, String gareDe, String gareA, int duree, int prix, int... heures) {
    }

    private static final List<Ligne> LIGNES = List.of(
            new Ligne("Dakar", "Saint-Louis", "Gare des Baux maraîchers", "Gare routière de Saint-Louis", 270, 6500, 6, 10, 15),
            new Ligne("Saint-Louis", "Dakar", "Gare routière de Saint-Louis", "Gare des Baux maraîchers", 270, 6500, 7, 14),
            new Ligne("Dakar", "Touba", "Gare des Baux maraîchers", "Gare routière de Touba", 240, 5000, 7, 12, 17),
            new Ligne("Touba", "Dakar", "Gare routière de Touba", "Gare des Baux maraîchers", 240, 5000, 6, 15),
            new Ligne("Dakar", "Thiès", "Gare des Baux maraîchers", "Gare routière de Thiès", 75, 2000, 6, 8, 11, 14, 18),
            new Ligne("Dakar", "Mbour", "Gare des Baux maraîchers", "Gare routière de Mbour", 90, 2500, 7, 13, 17),
            new Ligne("Dakar", "Kaolack", "Gare des Baux maraîchers", "Gare routière de Kaolack", 210, 4500, 6, 13),
            new Ligne("Dakar", "Ziguinchor", "Gare des Baux maraîchers", "Gare routière de Ziguinchor", 600, 12000, 5, 20),
            new Ligne("Dakar", "Tambacounda", "Gare des Baux maraîchers", "Gare routière de Tambacounda", 480, 10000, 6),
            new Ligne("Kaolack", "Tambacounda", "Gare routière de Kaolack", "Gare routière de Tambacounda", 270, 6000, 8),
            new Ligne("Thiès", "Saint-Louis", "Gare routière de Thiès", "Gare routière de Saint-Louis", 210, 5000, 9));

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (utilisateurRepository.count() > 0) {
            return;
        }
        utilisateurRepository.save(new Utilisateur("admin@yoon.sn", "Awa Ndiaye", "+221771000001",
                passwordEncoder.encode("Admin123!"), Role.ADMIN));
        utilisateurRepository.save(new Utilisateur("agent@yoon.sn", "Ousmane Diallo", "+221771000002",
                passwordEncoder.encode("Agent123!"), Role.AGENT));
        Utilisateur voyageur = utilisateurRepository.save(new Utilisateur("voyageur@yoon.sn", "Fatou Sow",
                "+221771000003", passwordEncoder.encode("Voyageur123!"), Role.VOYAGEUR));

        String[] immatriculations = {"DK-4521-BB", "DK-1187-AF", "TH-3302-C", "DK-9024-AZ", "SL-2210-B", "KL-0731-A"};
        int n = 0;
        Voyage demoDuJour = null;
        for (int jour = 0; jour < 14; jour++) {
            LocalDate date = LocalDate.now().plusDays(jour);
            for (Ligne l : LIGNES) {
                for (int h : l.heures()) {
                    Voyage v = new Voyage();
                    v.setVilleDepart(l.de());
                    v.setVilleArrivee(l.a());
                    v.setGareDepart(l.gareDe());
                    v.setGareArrivee(l.gareA());
                    v.setDateDepart(LocalDateTime.of(date, LocalTime.of(h, (n % 3) * 15)));
                    v.setDureeMinutes(l.duree());
                    ClasseVoyage classe = n % 5 == 0 ? ClasseVoyage.VIP : n % 3 == 0 ? ClasseVoyage.CONFORT : ClasseVoyage.STANDARD;
                    v.setClasse(classe);
                    v.setPrix(classe == ClasseVoyage.VIP ? l.prix() + 3000 : classe == ClasseVoyage.CONFORT ? l.prix() + 1500 : l.prix());
                    int capacite = classe == ClasseVoyage.VIP ? 30 : 60;
                    v.setPlacesTotal(capacite);
                    v.setPlacesDisponibles(capacite - (int) ((n * 7L) % (capacite / 2)));
                    v.setBus(immatriculations[n % immatriculations.length]);
                    voyageRepository.save(v);
                    n++;
                }
            }
        }
        // Départ de démonstration dans 2 heures, pour pouvoir scanner un billet tout de suite.
        demoDuJour = new Voyage();
        demoDuJour.setVilleDepart("Dakar");
        demoDuJour.setVilleArrivee("Saint-Louis");
        demoDuJour.setGareDepart("Gare des Baux maraîchers");
        demoDuJour.setGareArrivee("Gare routière de Saint-Louis");
        demoDuJour.setDateDepart(LocalDateTime.now().plusHours(2).withSecond(0).withNano(0));
        demoDuJour.setDureeMinutes(270);
        demoDuJour.setPrix(8000);
        demoDuJour.setClasse(ClasseVoyage.CONFORT);
        demoDuJour.setPlacesTotal(50);
        demoDuJour.setPlacesDisponibles(50);
        demoDuJour.setBus("DK-4521-BB");
        voyageRepository.save(demoDuJour);

        UtilisateurConnecte fatou = UtilisateurConnecte.of(voyageur);
        var reservation = reservationService.reserver(new ReservationRequest(demoDuJour.getId(),
                List.of(new PassagerRequest("Fatou Sow"), new PassagerRequest("Mamadou Sow"))), voyageur.getId());
        reservationService.payer(reservation.id(), new PaiementRequest(MethodePaiement.WAVE, "77 100 00 03"), fatou);

        log.info("Données de démo créées : {} départs. Comptes : admin@yoon.sn / Admin123!, "
                + "agent@yoon.sn / Agent123!, voyageur@yoon.sn / Voyageur123!", n + 1);
    }
}
