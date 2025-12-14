package moustapha.teach.gestionVoyage.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Module pour Java Time
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        mapper.registerModule(javaTimeModule);

        // Module personnalisé pour LocalDateTime
        SimpleModule localDateTimeModule = new SimpleModule();
        localDateTimeModule.addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeserializer());
        mapper.registerModule(localDateTimeModule);

        return mapper;
    }

    public static class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext context)
                throws IOException {
            String dateString = jsonParser.getText();

            if (dateString == null || dateString.trim().isEmpty()) {
                return null;
            }

            dateString = dateString.trim();

            try {
                // Cas 1: Format avec Z (UTC) - "2025-09-10T00:00:00.000Z"
                if (dateString.endsWith("Z")) {
                    OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateString);
                    return offsetDateTime.toLocalDateTime();
                }

                // Cas 2: Format avec millisecondes - "2025-09-10T00:00:00.000"
                if (dateString.contains(".") && !dateString.contains("+") && !dateString.contains("-")) {
                    return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
                }

                // Cas 3: Format standard - "2025-09-10T00:00:00"
                return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

            } catch (DateTimeParseException e) {
                // Dernière tentative avec l'analyseur ISO standard
                try {
                    return LocalDateTime.parse(dateString);
                } catch (DateTimeParseException e2) {
                    throw new IOException("Impossible de parser la date: '" + dateString +
                            "'. Formats acceptés: yyyy-MM-dd'T'HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss.SSS, yyyy-MM-dd'T'HH:mm:ss.SSSZ", e2);
                }
            }
        }
    }
}
