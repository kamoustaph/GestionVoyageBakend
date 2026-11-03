package moustapha.teach.gestionVoyage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moustapha.teach.gestionVoyage.config.AppProperties;
import moustapha.teach.gestionVoyage.domain.*;
import moustapha.teach.gestionVoyage.dto.*;
import moustapha.teach.gestionVoyage.repository.BilletRepository;
import moustapha.teach.gestionVoyage.repository.ReservationRepository;
import moustapha.teach.gestionVoyage.repository.UtilisateurRepository;
import moustapha.teach.gestionVoyage.security.UtilisateurConnecte;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Cycle de vie d'une réservation :
 * réserver (places bloquées) → payer (billets émis) → [annuler].
 * Une réservation non payée expire automatiquement et libère ses places.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BilletRepository billetRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final VoyageService voyageService;
    private final Mapper mapper;
    private final AppProperties props;

    @Transactional
    public ReservationDto reserver(ReservationRequest req, Long utilisateurId) {
        Voyage voyage = voyageService.charger(req.voyageId());
        int places = req.passagers().size();
        if (!voyage.estReservable()) {
            throw ApiException.conflit("Ce départ n'est plus ouvert à la réservation");
        }
        if (voyage.getPlacesDisponibles() < places) {
            throw ApiException.conflit(voyage.getPlacesDisponibles() == 0
                    ? "Ce car est complet"
                    : "Il ne reste que " + voyage.getPlacesDisponibles() + " place(s)");
        }
        // Le verrou optimiste (@Version) sur Voyage empêche deux réservations simultanées de survendre.
        voyage.setPlacesDisponibles(voyage.getPlacesDisponibles() - places);

        Reservation r = new Reservation();
        r.setReference(nouvelleReference());
        r.setUtilisateur(utilisateurRepository.getReferenceById(utilisateurId));
        r.setVoyage(voyage);
        r.setNombrePlaces(places);
        r.setMontantTotal(places * voyage.getPrix());
        r.setExpireLe(LocalDateTime.now().plusMinutes(props.reservation().delaiPaiementMinutes()));
        // Un billet EN_ATTENTE par passager ; il reçoit son siège et devient VALIDE au paiement.
        for (PassagerRequest p : req.passagers()) {
            Billet b = new Billet();
            b.setReservation(r);
            b.setVoyage(voyage);
            b.setNomPassager(p.nom().trim());
            b.setCode(nouveauCodeBillet());
            b.setSiege(0);
            b.setStatut(StatutBillet.EN_ATTENTE); // inactif tant que la réservation n'est pas payée
            r.getBillets().add(b);
        }
        return mapper.reservation(reservationRepository.save(r), false);
    }

    /**
     * Paiement simulé : dans un vrai déploiement, cette étape appellerait l'API de l'opérateur
     * (Wave, Orange Money, PayDunya...) et attendrait sa confirmation (webhook) avant d'émettre les billets.
     */
    @Transactional
    public ReservationDto payer(Long reservationId, PaiementRequest req, UtilisateurConnecte user) {
        Reservation r = chargerPourProprietaire(reservationId, user);
        if (r.getStatut() != StatutReservation.EN_ATTENTE_PAIEMENT) {
            throw ApiException.conflit("Cette réservation n'est pas en attente de paiement");
        }
        if (r.getExpireLe() != null && r.getExpireLe().isBefore(LocalDateTime.now())) {
            expirer(r);
            throw ApiException.conflit("Le délai de paiement est dépassé, les places ont été libérées");
        }
        boolean mobile = req.methode() == MethodePaiement.WAVE || req.methode() == MethodePaiement.ORANGE_MONEY
                || req.methode() == MethodePaiement.FREE_MONEY;
        if (mobile && (req.telephone() == null || req.telephone().replaceAll("\\D", "").length() < 9)) {
            throw ApiException.invalide("Indiquez le numéro du compte " + req.methode().name().replace('_', ' '));
        }

        Paiement p = new Paiement();
        p.setReservation(r);
        p.setMontant(r.getMontantTotal());
        p.setMethode(req.methode());
        p.setTelephone(mobile ? AuthService.normaliserTelephone(req.telephone()) : null);
        p.setTransactionRef(req.methode().name().substring(0, 2) + "-" + CodeGenerator.code(12));
        r.setPaiement(p);
        r.setStatut(StatutReservation.CONFIRMEE);
        r.setExpireLe(null);

        attribuerSieges(r);
        return mapper.reservation(r, true);
    }

    @Transactional
    public ReservationDto annuler(Long reservationId, UtilisateurConnecte user) {
        Reservation r = chargerPourProprietaire(reservationId, user);
        if (r.getStatut() == StatutReservation.ANNULEE || r.getStatut() == StatutReservation.EXPIREE) {
            throw ApiException.conflit("Cette réservation est déjà close");
        }
        if (r.getBillets().stream().anyMatch(b -> b.getStatut() == StatutBillet.UTILISE)) {
            throw ApiException.conflit("Un passager a déjà embarqué : annulation impossible");
        }
        if (!r.getVoyage().getDateDepart().isAfter(LocalDateTime.now())) {
            throw ApiException.conflit("Le car est déjà parti");
        }
        r.setStatut(StatutReservation.ANNULEE);
        r.getBillets().forEach(b -> b.setStatut(StatutBillet.ANNULE));
        Voyage v = r.getVoyage();
        v.setPlacesDisponibles(Math.min(v.getPlacesTotal(), v.getPlacesDisponibles() + r.getNombrePlaces()));
        return mapper.reservation(r, true);
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> mesReservations(Long utilisateurId) {
        return reservationRepository.findByUtilisateurIdOrderByCreeLeDesc(utilisateurId).stream()
                .map(r -> mapper.reservation(r, r.getStatut() == StatutReservation.CONFIRMEE))
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationDto detail(Long id, UtilisateurConnecte user) {
        Reservation r = chargerPourProprietaire(id, user);
        return mapper.reservation(r, r.getStatut() == StatutReservation.CONFIRMEE);
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> toutes() {
        return reservationRepository.findTop200ByOrderByCreeLeDesc().stream()
                .map(r -> mapper.reservation(r, false)).toList();
    }

    @Transactional(readOnly = true)
    public List<BilletDto> mesBillets(Long utilisateurId) {
        return billetRepository.findDuVoyageur(utilisateurId).stream()
                .filter(b -> b.getStatut() == StatutBillet.VALIDE || b.getStatut() == StatutBillet.UTILISE)
                .map(b -> mapper.billet(b, b.getStatut() == StatutBillet.VALIDE))
                .toList();
    }

    @Transactional(readOnly = true)
    public BilletDto billet(String code, UtilisateurConnecte user) {
        Billet b = billetRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> ApiException.introuvable("Billet"));
        boolean proprietaire = b.getReservation().getUtilisateur().getId().equals(user.id());
        boolean personnel = !user.role().equals(Role.VOYAGEUR.name());
        if (!proprietaire && !personnel) {
            throw ApiException.introuvable("Billet");
        }
        return mapper.billet(b, proprietaire && b.getStatut() == StatutBillet.VALIDE);
    }

    /** Toutes les minutes : les réservations non payées dans le délai expirent et rendent leurs places. */
    @Scheduled(fixedDelayString = "PT1M", initialDelayString = "PT30S")
    @Transactional
    public void expirerReservationsNonPayees() {
        List<Reservation> expirees = reservationRepository
                .findByStatutAndExpireLeBefore(StatutReservation.EN_ATTENTE_PAIEMENT, LocalDateTime.now());
        expirees.forEach(this::expirer);
        if (!expirees.isEmpty()) {
            log.info("{} réservation(s) expirée(s), places libérées", expirees.size());
        }
    }

    private void expirer(Reservation r) {
        r.setStatut(StatutReservation.EXPIREE);
        r.getBillets().forEach(b -> b.setStatut(StatutBillet.ANNULE));
        Voyage v = r.getVoyage();
        v.setPlacesDisponibles(Math.min(v.getPlacesTotal(), v.getPlacesDisponibles() + r.getNombrePlaces()));
    }

    /** Attribue les plus petits numéros de siège libres et active les billets. */
    private void attribuerSieges(Reservation r) {
        Set<Integer> occupes = new HashSet<>();
        billetRepository.findByVoyageIdAndStatutInOrderBySiegeAsc(r.getVoyage().getId(),
                        List.of(StatutBillet.VALIDE, StatutBillet.UTILISE))
                .forEach(b -> occupes.add(b.getSiege()));
        int siege = 1;
        for (Billet b : r.getBillets()) {
            while (occupes.contains(siege)) {
                siege++;
            }
            b.setSiege(siege);
            b.setStatut(StatutBillet.VALIDE);
            occupes.add(siege);
        }
    }

    private Reservation chargerPourProprietaire(Long id, UtilisateurConnecte user) {
        Reservation r = reservationRepository.findWithDetailsById(id)
                .orElseThrow(() -> ApiException.introuvable("Réservation"));
        boolean admin = user.role().equals(Role.ADMIN.name());
        if (!admin && !r.getUtilisateur().getId().equals(user.id())) {
            throw ApiException.introuvable("Réservation");
        }
        return r;
    }

    private String nouvelleReference() {
        String ref;
        do {
            ref = "YN-" + CodeGenerator.code(6);
        } while (reservationRepository.existsByReference(ref));
        return ref;
    }

    private String nouveauCodeBillet() {
        String code;
        do {
            code = CodeGenerator.code(10);
        } while (billetRepository.existsByCode(code));
        return code;
    }
}
