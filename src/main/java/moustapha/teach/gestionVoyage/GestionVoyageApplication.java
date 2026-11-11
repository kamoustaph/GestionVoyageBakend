package moustapha.teach.gestionVoyage;

import moustapha.teach.gestionVoyage.config.FuseauConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class GestionVoyageApplication {

    // Heure de Dakar partout, même si la machine est réglée sur un autre fuseau (poste à Paris, conteneur en UTC).
    // Bloc statique : appliqué dès le chargement de la classe, y compris dans les tests qui n'appellent pas main().
    static {
        FuseauConfig.appliquerFuseauParDefaut();
    }

    public static void main(String[] args) {
        SpringApplication.run(GestionVoyageApplication.class, args);
    }
}
