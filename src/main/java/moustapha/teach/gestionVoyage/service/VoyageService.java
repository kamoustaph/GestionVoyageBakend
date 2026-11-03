package moustapha.teach.gestionVoyage.service;

import lombok.RequiredArgsConstructor;
import moustapha.teach.gestionVoyage.domain.*;
import moustapha.teach.gestionVoyage.dto.PassagerManifesteDto;
import moustapha.teach.gestionVoyage.dto.VoyageDto;
import moustapha.teach.gestionVoyage.dto.VoyageRequest;
import moustapha.teach.gestionVoyage.repository.BilletRepository;
import moustapha.teach.gestionVoyage.repository.ReservationRepository;
import moustapha.teach.gestionVoyage.repository.VoyageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoyageService {

    /** Villes desservies (principales gares routières du Sénégal). */
    public static final List<String> VILLES = List.of(
            "Dakar", "Thiès", "Mbour", "Saint-Louis", "Louga", "Touba", "Diourbel", "Kaolack",
            "Fatick", "Kaffrine", "Tambacounda", "Kédougou", "Kolda", "Sédhiou", "Ziguinchor", "Matam", "Podor");

    private static final EnumSet<StatutVoyage> VISIBLES = EnumSet.of(StatutVoyage.PROGRAMME, StatutVoyage.EMBARQUEMENT);

    private final VoyageRepository voyageRepository;
    private final BilletRepository billetRepository;
    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public List<VoyageDto> rechercher(String depart, String arrivee, LocalDate date) {
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime debut = date == null || !date.isAfter(LocalDate.now()) ? maintenant : date.atStartOfDay();
        LocalDateTime fin = date == null ? debut.plusDays(30) : date.plusDays(1).atStartOfDay();
        return voyageRepository.rechercher(vide(depart), vide(arrivee), debut, fin, VISIBLES)
                .stream().map(VoyageDto::of).toList();
    }

    @Transactional(readOnly = true)
    public List<VoyageDto> prochainsDeparts(int limite) {
        return voyageRepository.findByDateDepartAfterAndStatutInOrderByDateDepartAsc(
                        LocalDateTime.now(), VISIBLES, PageRequest.of(0, Math.min(limite, 30)))
                .stream().map(VoyageDto::of).toList();
    }

    @Transactional(readOnly = true)
    public VoyageDto detail(Long id) {
        return VoyageDto.of(charger(id));
    }

    /** Départs d'aujourd'hui et de demain matin, pour que l'agent choisisse le car qu'il contrôle. */
    @Transactional(readOnly = true)
    public List<VoyageDto> departsAControler() {
        LocalDateTime debut = LocalDate.now().atStartOfDay();
        return voyageRepository.findByDateDepartBetweenOrderByDateDepartAsc(debut, debut.plusHours(36))
                .stream().filter(v -> v.getStatut() != StatutVoyage.ANNULE).map(VoyageDto::of).toList();
    }

    @Transactional(readOnly = true)
    public List<PassagerManifesteDto> manifeste(Long voyageId) {
        charger(voyageId);
        return billetRepository.findByVoyageIdAndStatutInOrderBySiegeAsc(voyageId,
                        List.of(StatutBillet.VALIDE, StatutBillet.UTILISE)).stream()
                .map(b -> new PassagerManifesteDto(b.getCode(), b.getSiege(), b.getNomPassager(),
                        b.getReservation().getReference(), b.getReservation().getUtilisateur().getTelephone(),
                        b.getStatut(), b.getEmbarqueLe()))
                .toList();
    }

    // ---------- Administration ----------

    @Transactional(readOnly = true)
    public List<VoyageDto> tous() {
        return voyageRepository.findAllByOrderByDateDepartDesc().stream().map(VoyageDto::of).toList();
    }

    @Transactional
    public VoyageDto creer(VoyageRequest req) {
        Voyage v = new Voyage();
        appliquer(v, req);
        v.setPlacesDisponibles(req.placesTotal());
        return VoyageDto.of(voyageRepository.save(v));
    }

    @Transactional
    public VoyageDto modifier(Long id, VoyageRequest req) {
        Voyage v = charger(id);
        if (v.getStatut() == StatutVoyage.ANNULE || v.getStatut() == StatutVoyage.PARTI) {
            throw ApiException.conflit("Un voyage parti ou annulé ne peut plus être modifié");
        }
        int vendues = v.getPlacesTotal() - v.getPlacesDisponibles();
        if (req.placesTotal() < vendues) {
            throw ApiException.conflit(vendues + " places sont déjà vendues : capacité insuffisante");
        }
        appliquer(v, req);
        v.setPlacesDisponibles(req.placesTotal() - vendues);
        return VoyageDto.of(v);
    }

    @Transactional
    public VoyageDto changerStatut(Long id, StatutVoyage statut) {
        Voyage v = charger(id);
        if (v.getStatut() == StatutVoyage.ANNULE) {
            throw ApiException.conflit("Ce voyage est déjà annulé");
        }
        v.setStatut(statut);
        if (statut == StatutVoyage.ANNULE) {
            // Annulation : réservations et billets annulés (remboursement à traiter hors application).
            for (Reservation r : reservationRepository.findByVoyageIdAndStatutIn(id,
                    List.of(StatutReservation.EN_ATTENTE_PAIEMENT, StatutReservation.CONFIRMEE))) {
                r.setStatut(StatutReservation.ANNULEE);
                r.getBillets().forEach(b -> {
                    if (b.getStatut() == StatutBillet.VALIDE || b.getStatut() == StatutBillet.EN_ATTENTE) {
                        b.setStatut(StatutBillet.ANNULE);
                    }
                });
            }
        }
        return VoyageDto.of(v);
    }

    Voyage charger(Long id) {
        return voyageRepository.findById(id).orElseThrow(() -> ApiException.introuvable("Voyage"));
    }

    private void appliquer(Voyage v, VoyageRequest req) {
        if (req.villeDepart().equalsIgnoreCase(req.villeArrivee())) {
            throw ApiException.invalide("Les villes de départ et d'arrivée doivent être différentes");
        }
        v.setVilleDepart(req.villeDepart().trim());
        v.setVilleArrivee(req.villeArrivee().trim());
        v.setGareDepart(req.gareDepart());
        v.setGareArrivee(req.gareArrivee());
        v.setDateDepart(req.dateDepart());
        v.setDureeMinutes(req.dureeMinutes());
        v.setPrix(req.prix());
        v.setPlacesTotal(req.placesTotal());
        v.setClasse(req.classe());
        v.setBus(req.bus());
    }

    private static String vide(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
