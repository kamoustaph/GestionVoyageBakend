package moustapha.teach.gestionVoyage.repository;

import moustapha.teach.gestionVoyage.domain.Controle;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ControleRepository extends JpaRepository<Controle, Long> {

    @EntityGraph(attributePaths = {"agent", "billet", "billet.voyage"})
    List<Controle> findByAgentIdAndScanneLeAfterOrderByScanneLeDesc(Long agentId, LocalDateTime apres);

    @EntityGraph(attributePaths = {"agent", "billet", "billet.voyage"})
    List<Controle> findTop200ByOrderByScanneLeDesc();

    long countByScanneLeAfter(LocalDateTime apres);

    long countByScanneLeAfterAndResultatNot(LocalDateTime apres,
                                            moustapha.teach.gestionVoyage.domain.ResultatControle resultat);
}
