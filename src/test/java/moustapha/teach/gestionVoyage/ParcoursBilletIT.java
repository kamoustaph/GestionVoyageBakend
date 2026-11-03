package moustapha.teach.gestionVoyage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import moustapha.teach.gestionVoyage.domain.*;
import moustapha.teach.gestionVoyage.repository.UtilisateurRepository;
import moustapha.teach.gestionVoyage.repository.VoyageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Parcours complet : inscription → réservation → paiement → billet QR → contrôle par un agent.
 * Vérifie aussi les refus : double scan, QR falsifié, accès sans le bon rôle.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ParcoursBilletIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired VoyageRepository voyageRepository;
    @Autowired UtilisateurRepository utilisateurRepository;
    @Autowired PasswordEncoder passwordEncoder;

    Voyage voyage;

    @BeforeEach
    void setUp() {
        voyage = new Voyage();
        voyage.setVilleDepart("Dakar");
        voyage.setVilleArrivee("Saint-Louis");
        voyage.setDateDepart(LocalDateTime.now().plusHours(3));
        voyage.setDureeMinutes(270);
        voyage.setPrix(6500);
        voyage.setPlacesTotal(3);
        voyage.setPlacesDisponibles(3);
        voyage.setClasse(ClasseVoyage.STANDARD);
        voyage = voyageRepository.save(voyage);
        String agent = "agent" + System.nanoTime() + "@yoon.sn";
        utilisateurRepository.save(new Utilisateur(agent, "Agent Test", null, passwordEncoder.encode("Agent123!"), Role.AGENT));
        agentEmail = agent;
    }

    String agentEmail;

    @Test
    void parcoursCompletDuBilletJusquAEmbarquement() throws Exception {
        String tokenVoyageur = inscrire();
        String tokenAgent = connecter(agentEmail, "Agent123!");

        // Recherche publique
        mvc.perform(get("/api/voyages").param("depart", "dakar").param("arrivee", "Saint-Louis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + voyage.getId() + ")]").exists());

        // Réservation de 2 places
        JsonNode reservation = lire(posterAvec(tokenVoyageur, "/api/reservations", Map.of(
                "voyageId", voyage.getId(),
                "passagers", new Object[]{Map.of("nom", "Fatou Sow"), Map.of("nom", "Ibrahima Sow")}))
                .andExpect(status().isCreated()));
        assertThat(reservation.get("statut").asText()).isEqualTo("EN_ATTENTE_PAIEMENT");
        assertThat(reservation.get("montantTotal").asInt()).isEqualTo(13000);
        assertThat(voyageRepository.findById(voyage.getId()).orElseThrow().getPlacesDisponibles()).isEqualTo(1);

        // Trop de places demandées
        posterAvec(tokenVoyageur, "/api/reservations", Map.of("voyageId", voyage.getId(),
                "passagers", new Object[]{Map.of("nom", "A B"), Map.of("nom", "C D")}))
                .andExpect(status().isConflict());

        // Paiement Wave
        JsonNode payee = lire(posterAvec(tokenVoyageur, "/api/reservations/" + reservation.get("id").asLong() + "/paiement",
                Map.of("methode", "WAVE", "telephone", "77 123 45 67")).andExpect(status().isOk()));
        assertThat(payee.get("statut").asText()).isEqualTo("CONFIRMEE");
        JsonNode billet = payee.get("billets").get(0);
        String qr = billet.get("qrContenu").asText();
        assertThat(qr).startsWith("YOON1.");
        assertThat(billet.get("siege").asInt()).isEqualTo(1);

        // Un voyageur ne peut pas contrôler
        posterAvec(tokenVoyageur, "/api/controle/verification", Map.of("contenu", qr)).andExpect(status().isForbidden());

        // Vérification par l'agent : accepté, nom du passager visible
        posterAvec(tokenAgent, "/api/controle/verification", Map.of("contenu", qr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultat").value("ACCEPTE"))
                .andExpect(jsonPath("$.billet.nomPassager").value("Fatou Sow"))
                .andExpect(jsonPath("$.billet.qrContenu").doesNotExist());

        // Mauvais départ sélectionné
        posterAvec(tokenAgent, "/api/controle/verification", Map.of("contenu", qr, "voyageId", voyage.getId() + 999))
                .andExpect(jsonPath("$.resultat").value("MAUVAIS_VOYAGE"));

        // QR falsifié : signature modifiée
        String falsifie = qr.substring(0, qr.length() - 2) + (qr.endsWith("AA") ? "BB" : "AA");
        posterAvec(tokenAgent, "/api/controle/verification", Map.of("contenu", falsifie))
                .andExpect(jsonPath("$.resultat").value("SIGNATURE_INVALIDE"))
                .andExpect(jsonPath("$.billet").doesNotExist());

        // QR étranger
        posterAvec(tokenAgent, "/api/controle/verification", Map.of("contenu", "https://example.com"))
                .andExpect(jsonPath("$.resultat").value("FORMAT_INVALIDE"));

        // Embarquement puis second scan refusé
        posterAvec(tokenAgent, "/api/controle/embarquement", Map.of("contenu", qr))
                .andExpect(jsonPath("$.resultat").value("ACCEPTE"));
        posterAvec(tokenAgent, "/api/controle/embarquement", Map.of("contenu", qr))
                .andExpect(jsonPath("$.resultat").value("DEJA_UTILISE"))
                .andExpect(jsonPath("$.accepte").value(false));

        // Saisie manuelle du code du second billet
        posterAvec(tokenAgent, "/api/controle/verification",
                Map.of("contenu", payee.get("billets").get(1).get("code").asText()))
                .andExpect(jsonPath("$.resultat").value("ACCEPTE"));

        // Liste d'embarquement et historique
        mvc.perform(get("/api/controle/departs/" + voyage.getId() + "/passagers").header("Authorization", "Bearer " + tokenAgent))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].statut").value("UTILISE"));
        mvc.perform(get("/api/controle/historique").header("Authorization", "Bearer " + tokenAgent))
                .andExpect(jsonPath("$.length()").value(5));

        // Annulation impossible après embarquement
        posterAvec(tokenVoyageur, "/api/reservations/" + reservation.get("id").asLong() + "/annulation", Map.of())
                .andExpect(status().isConflict());
    }

    @Test
    void accesProtegesEtStatistiques() throws Exception {
        mvc.perform(get("/api/reservations/miennes")).andExpect(status().isUnauthorized());
        String token = inscrire();
        mvc.perform(get("/api/admin/statistiques").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        utilisateurRepository.save(new Utilisateur("admin-test@yoon.sn", "Admin", null,
                passwordEncoder.encode("Admin123!"), Role.ADMIN));
        String admin = connecter("admin-test@yoon.sn", "Admin123!");
        mvc.perform(get("/api/admin/statistiques").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ventes14Jours.length()").value(14));
    }

    @Test
    void connexionRefuseeAvecMauvaisMotDePasse() throws Exception {
        mvc.perform(post("/api/auth/connexion").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("email", agentEmail, "motDePasse", "faux"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email ou mot de passe incorrect"));
    }

    private String inscrire() throws Exception {
        String email = "v" + System.nanoTime() + "@exemple.sn";
        return lire(mvc.perform(post("/api/auth/inscription").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("email", email, "nomComplet", "Fatou Sow",
                                "telephone", "77 123 45 67", "motDePasse", "Voyageur123!"))))
                .andExpect(status().isCreated())).get("token").asText();
    }

    private String connecter(String email, String mdp) throws Exception {
        return lire(mvc.perform(post("/api/auth/connexion").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("email", email, "motDePasse", mdp))))
                .andExpect(status().isOk())).get("token").asText();
    }

    private ResultActions posterAvec(String token, String url, Object body) throws Exception {
        return mvc.perform(post(url).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body)));
    }

    private JsonNode lire(ResultActions r) throws Exception {
        return json.readTree(r.andReturn().getResponse().getContentAsString());
    }
}
