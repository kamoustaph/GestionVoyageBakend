package moustapha.teach.gestionVoyage.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.TimeZone;

/**
 * Fuseau horaire de l'application : Africa/Dakar (UTC+0, sans heure d'été).
 *
 * <p>Les entités gardent des {@link LocalDateTime} : ce sont des heures de Dakar (l'horaire d'un car).
 * Mais un {@code LocalDateTime} envoyé tel quel, par exemple {@code "2026-11-11T12:15:00"}, est lu par un
 * navigateur comme une heure de SON fuseau : à Paris (UTC+1), le délai de paiement paraissait expiré dès
 * l'ouverture. L'API envoie donc toujours le décalage : {@code "2026-11-11T12:15:00Z"}.
 *
 * <p>En entrée, une date sans décalage (champ date/heure d'un formulaire) est une heure de Dakar ;
 * une date avec décalage est convertie en heure de Dakar.
 */
@Configuration
public class FuseauConfig {

    public static final ZoneId ZONE = ZoneId.of("Africa/Dakar");

    /**
     * Fixe le fuseau par défaut de la JVM, pour que {@code LocalDateTime.now()} donne l'heure de Dakar
     * quel que soit le serveur (poste Windows à Paris, conteneur en UTC...). Appelé avant le démarrage de Spring.
     */
    public static void appliquerFuseauParDefaut() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZONE));
    }

    @Bean
    Jackson2ObjectMapperBuilderCustomizer datesAvecFuseau() {
        return builder -> builder
                .serializerByType(LocalDateTime.class, new Ecriture())
                .deserializerByType(LocalDateTime.class, new Lecture());
    }

    static final class Ecriture extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime valeur, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(valeur.atZone(ZONE).toOffsetDateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }
    }

    static final class Lecture extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            String texte = p.getValueAsString();
            if (texte == null || texte.isBlank()) {
                return null;
            }
            texte = texte.trim();
            try {
                return OffsetDateTime.parse(texte).atZoneSameInstant(ZONE).toLocalDateTime();
            } catch (DateTimeParseException sansDecalage) {
                try {
                    return LocalDateTime.parse(texte);
                } catch (DateTimeParseException e) {
                    return (LocalDateTime) ctx.handleWeirdStringValue(LocalDateTime.class, texte,
                            "date attendue au format AAAA-MM-JJTHH:MM[:SS][décalage]");
                }
            }
        }
    }
}
