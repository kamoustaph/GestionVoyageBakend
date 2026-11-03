package moustapha.teach.gestionVoyage.service;

import lombok.RequiredArgsConstructor;
import moustapha.teach.gestionVoyage.domain.*;
import moustapha.teach.gestionVoyage.dto.CreerUtilisateurRequest;
import moustapha.teach.gestionVoyage.dto.StatsDto;
import moustapha.teach.gestionVoyage.dto.UtilisateurDto;
import moustapha.teach.gestionVoyage.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UtilisateurRepository utilisateurRepository;
    private final ReservationRepository reservationRepository;
    private final BilletRepository billetRepository;
    private final VoyageRepository voyageRepository;
    private final ControleRepository controleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UtilisateurDto> utilisateurs() {
        return utilisateurRepository.findAllByOrderByCreeLeDesc().stream().map(UtilisateurDto::of).toList();
    }

    @Transactional
    public UtilisateurDto creerUtilisateur(CreerUtilisateurRequest req) {
        String email = req.email().trim().toLowerCase();
        if (utilisateurRepository.existsByEmailIgnoreCase(email)) {
            throw ApiException.conflit("Un compte existe déjà avec cet email");
        }
        Utilisateur u = new Utilisateur(email, req.nomComplet().trim(), AuthService.normaliserTelephone(req.telephone()),
                passwordEncoder.encode(req.motDePasse()), req.role());
        return UtilisateurDto.of(utilisateurRepository.save(u));
    }

    @Transactional
    public UtilisateurDto modifier(Long id, Boolean actif, Role role, Long adminId) {
        Utilisateur u = utilisateurRepository.findById(id).orElseThrow(() -> ApiException.introuvable("Compte"));
        if (u.getId().equals(adminId)) {
            throw ApiException.conflit("Vous ne pouvez pas modifier votre propre compte ici");
        }
        if (actif != null) {
            u.setActif(actif);
        }
        if (role != null) {
            u.setRole(role);
        }
        return UtilisateurDto.of(u);
    }

    @Transactional(readOnly = true)
    public StatsDto statistiques() {
        LocalDateTime aujourdhui = LocalDate.now().atStartOfDay();
        LocalDate debut14 = LocalDate.now().minusDays(13);

        Map<LocalDate, long[]> parJour = new HashMap<>();
        for (Object[] row : reservationRepository.ventesParJour(debut14.atStartOfDay())) {
            parJour.put((LocalDate) row[0], new long[]{((Number) row[1]).longValue(), ((Number) row[2]).longValue()});
        }
        List<StatsDto.VenteJour> ventes = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            LocalDate jour = debut14.plusDays(i);
            long[] v = parJour.getOrDefault(jour, new long[]{0, 0});
            ventes.add(new StatsDto.VenteJour(jour.toString(), v[0], v[1]));
        }

        List<StatsDto.Trajet> top = reservationRepository.topTrajets().stream().limit(6)
                .map(r -> new StatsDto.Trajet((String) r[0], (String) r[1],
                        ((Number) r[2]).longValue(), ((Number) r[3]).longValue()))
                .toList();

        long vendus = billetRepository.countByStatut(StatutBillet.VALIDE) + billetRepository.countByStatut(StatutBillet.UTILISE);
        List<Voyage> aVenir = voyageRepository.findByDateDepartBetweenOrderByDateDepartAsc(
                LocalDateTime.now(), LocalDateTime.now().plusDays(30));
        long capacite = aVenir.stream().filter(v -> v.getStatut() != StatutVoyage.ANNULE).mapToLong(Voyage::getPlacesTotal).sum();
        long occupees = aVenir.stream().filter(v -> v.getStatut() != StatutVoyage.ANNULE)
                .mapToLong(v -> v.getPlacesTotal() - v.getPlacesDisponibles()).sum();

        return new StatsDto(
                reservationRepository.chiffreAffairesTotal(),
                vendus,
                billetRepository.countByStatut(StatutBillet.UTILISE),
                voyageRepository.countByDateDepartAfterAndStatutNot(LocalDateTime.now(), StatutVoyage.ANNULE),
                utilisateurRepository.countByRole(Role.VOYAGEUR),
                utilisateurRepository.countByRole(Role.AGENT),
                controleRepository.countByScanneLeAfter(aujourdhui),
                controleRepository.countByScanneLeAfterAndResultatNot(aujourdhui, ResultatControle.ACCEPTE),
                capacite == 0 ? 0 : (int) Math.round(100.0 * occupees / capacite),
                ventes,
                top);
    }
}
