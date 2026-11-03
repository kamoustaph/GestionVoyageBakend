package moustapha.teach.gestionVoyage.service;

import lombok.RequiredArgsConstructor;
import moustapha.teach.gestionVoyage.config.AppProperties;
import moustapha.teach.gestionVoyage.domain.*;
import moustapha.teach.gestionVoyage.dto.ControleDto;
import moustapha.teach.gestionVoyage.dto.ControleRequest;
import moustapha.teach.gestionVoyage.dto.ControleResultatDto;
import moustapha.teach.gestionVoyage.repository.BilletRepository;
import moustapha.teach.gestionVoyage.repository.ControleRepository;
import moustapha.teach.gestionVoyage.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Contrôle des billets à l'embarquement, en deux temps :
 * <ol>
 *   <li>{@link #verifier} : l'agent scanne, le serveur renvoie le verdict et le nom du passager
 *       (l'agent peut comparer avec la pièce d'identité). Le billet n'est pas consommé.</li>
 *   <li>{@link #embarquer} : l'agent confirme, le billet passe à UTILISE. Un second scan sera refusé.</li>
 * </ol>
 * Chaque refus et chaque embarquement sont enregistrés dans le journal des contrôles.
 */
@Service
@RequiredArgsConstructor
public class ControleService {

    private final BilletRepository billetRepository;
    private final ControleRepository controleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final QrSigner qrSigner;
    private final Mapper mapper;
    private final AppProperties props;

    @Transactional
    public ControleResultatDto verifier(ControleRequest req, Long agentId) {
        Analyse analyse = analyser(req);
        if (!analyse.resultat().isAccepte()) {
            journaliser(agentId, analyse, req.contenu());
        }
        return reponse(analyse, analyse.resultat().getLibelle());
    }

    @Transactional
    public ControleResultatDto embarquer(ControleRequest req, Long agentId) {
        Analyse analyse = analyser(req);
        if (analyse.resultat().isAccepte()) {
            Billet b = analyse.billet();
            b.setStatut(StatutBillet.UTILISE);
            b.setEmbarqueLe(LocalDateTime.now());
            b.setEmbarquePar(utilisateurRepository.getReferenceById(agentId));
            if (b.getVoyage().getStatut() == StatutVoyage.PROGRAMME) {
                b.getVoyage().setStatut(StatutVoyage.EMBARQUEMENT);
            }
        }
        journaliser(agentId, analyse, req.contenu());
        String message = analyse.resultat().isAccepte()
                ? "Embarquement enregistré, siège " + analyse.billet().getSiege()
                : analyse.resultat().getLibelle();
        return reponse(analyse, message);
    }

    @Transactional(readOnly = true)
    public List<ControleDto> historiqueDuJour(Long agentId) {
        return controleRepository
                .findByAgentIdAndScanneLeAfterOrderByScanneLeDesc(agentId, LocalDate.now().atStartOfDay())
                .stream().map(mapper::controle).toList();
    }

    @Transactional(readOnly = true)
    public List<ControleDto> derniers() {
        return controleRepository.findTop200ByOrderByScanneLeDesc().stream().map(mapper::controle).toList();
    }

    private Analyse analyser(ControleRequest req) {
        Optional<QrSigner.Contenu> contenu = qrSigner.lire(req.contenu());
        if (contenu.isEmpty()) {
            return new Analyse(ResultatControle.FORMAT_INVALIDE, null);
        }
        Optional<Billet> trouve = billetRepository.findByCode(contenu.get().code());
        if (trouve.isEmpty()) {
            return new Analyse(ResultatControle.INCONNU, null);
        }
        Billet billet = trouve.get();
        String signature = contenu.get().signature();
        if (signature != null && !qrSigner.signatureValide(billet, signature)) {
            return new Analyse(ResultatControle.SIGNATURE_INVALIDE, null);
        }
        Voyage voyage = billet.getVoyage();
        if (voyage.getStatut() == StatutVoyage.ANNULE) {
            return new Analyse(ResultatControle.VOYAGE_ANNULE, billet);
        }
        if (billet.getStatut() == StatutBillet.ANNULE) {
            return new Analyse(ResultatControle.BILLET_ANNULE, billet);
        }
        if (billet.getStatut() == StatutBillet.EN_ATTENTE) {
            return new Analyse(ResultatControle.NON_PAYE, billet);
        }
        if (billet.getStatut() == StatutBillet.UTILISE) {
            return new Analyse(ResultatControle.DEJA_UTILISE, billet);
        }
        if (req.voyageId() != null && !req.voyageId().equals(voyage.getId())) {
            return new Analyse(ResultatControle.MAUVAIS_VOYAGE, billet);
        }
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime ouverture = voyage.getDateDepart().minusHours(props.controle().heuresAvantDepart());
        LocalDateTime fermeture = voyage.getDateDepart().plusHours(props.controle().heuresApresDepart());
        if (maintenant.isBefore(ouverture) || maintenant.isAfter(fermeture)) {
            return new Analyse(ResultatControle.HORS_DELAI, billet);
        }
        return new Analyse(ResultatControle.ACCEPTE, billet);
    }

    private void journaliser(Long agentId, Analyse analyse, String contenu) {
        Controle c = new Controle();
        c.setAgent(utilisateurRepository.getReferenceById(agentId));
        c.setBillet(analyse.billet());
        c.setResultat(analyse.resultat());
        c.setContenuScanne(contenu == null ? null : contenu.substring(0, Math.min(contenu.length(), 120)));
        controleRepository.save(c);
    }

    private ControleResultatDto reponse(Analyse analyse, String message) {
        return new ControleResultatDto(analyse.resultat(), analyse.resultat().isAccepte(), message,
                analyse.billet() == null ? null : mapper.billet(analyse.billet(), false));
    }

    /** @param billet null si le billet n'a pas pu être identifié de façon fiable. */
    private record Analyse(ResultatControle resultat, Billet billet) {
    }
}
