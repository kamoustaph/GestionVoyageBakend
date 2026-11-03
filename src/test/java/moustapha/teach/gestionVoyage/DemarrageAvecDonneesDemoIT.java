package moustapha.teach.gestionVoyage;

import moustapha.teach.gestionVoyage.repository.BilletRepository;
import moustapha.teach.gestionVoyage.repository.VoyageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/** L'application démarre avec les données de démonstration (profil par défaut). */
@SpringBootTest(properties = {"app.demo.enabled=true", "spring.datasource.url=jdbc:h2:mem:demo;MODE=MySQL;DB_CLOSE_DELAY=-1"})
class DemarrageAvecDonneesDemoIT {

    @Autowired VoyageRepository voyageRepository;
    @Autowired BilletRepository billetRepository;

    @Test
    void lesDonneesDeDemoSontCreees() {
        assertThat(voyageRepository.count()).isGreaterThan(300);
        assertThat(billetRepository.count()).isEqualTo(2);
    }
}
