package moustapha.teach.gestionVoyage.repository;

import moustapha.teach.gestionVoyage.domain.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
}
