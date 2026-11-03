package moustapha.teach.gestionVoyage.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Paramètres de l'application, préfixe {@code app.} dans application.yml.
 * En production, les secrets viennent des variables d'environnement (JWT_SECRET, QR_SECRET).
 */
@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, Qr qr, Cors cors, Reservation reservation, Controle controle, Demo demo) {

    public record Jwt(@NotBlank String secret, long expirationMinutes) {
    }

    /** Clé HMAC qui signe le contenu des QR codes. Changer cette clé invalide tous les billets émis. */
    public record Qr(@NotBlank String secret) {
    }

    public record Cors(List<String> allowedOrigins) {
    }

    public record Reservation(int delaiPaiementMinutes) {
    }

    /** Fenêtre de validité d'un billet autour de l'heure de départ. */
    public record Controle(int heuresAvantDepart, int heuresApresDepart) {
    }

    public record Demo(boolean enabled) {
    }
}
