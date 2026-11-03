package moustapha.teach.gestionVoyage.service;

import moustapha.teach.gestionVoyage.config.AppProperties;
import moustapha.teach.gestionVoyage.domain.Billet;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

/**
 * Contenu du QR code d'un billet : {@code YOON1.<code>.<signature>}.
 *
 * <p>La signature est un HMAC-SHA256 (tronqué à 128 bits, encodé base64url) calculé sur
 * le code du billet, l'identifiant du voyage et le numéro de siège, avec une clé connue du serveur seul.
 * Conséquences :</p>
 * <ul>
 *   <li>un QR inventé ou recopié avec un autre code est rejeté (SIGNATURE_INVALIDE) ;</li>
 *   <li>si quelqu'un modifiait en base le voyage ou le siège d'un billet, l'ancien QR ne serait plus valide ;</li>
 *   <li>le QR ne contient aucune donnée personnelle : le nom du passager est lu côté serveur.</li>
 * </ul>
 */
@Component
public class QrSigner {

    public static final String PREFIXE = "YOON1";

    private final byte[] cle;

    public QrSigner(AppProperties props) {
        this.cle = props.qr().secret().getBytes(StandardCharsets.UTF_8);
    }

    public String contenu(Billet billet) {
        return PREFIXE + "." + billet.getCode() + "." + signature(billet);
    }

    /** Extrait le code du billet d'un contenu scanné, ou d'un code saisi à la main. */
    public Optional<Contenu> lire(String brut) {
        String s = brut == null ? "" : brut.trim();
        String[] parties = s.split("\\.");
        if (parties.length == 3 && PREFIXE.equals(parties[0]) && !parties[1].isBlank()) {
            return Optional.of(new Contenu(parties[1].toUpperCase(), parties[2]));
        }
        // Saisie manuelle du code imprimé sous le QR (10 caractères) : pas de signature à vérifier.
        if (s.matches("(?i)[A-Z0-9]{10}")) {
            return Optional.of(new Contenu(s.toUpperCase(), null));
        }
        return Optional.empty();
    }

    public boolean signatureValide(Billet billet, String signature) {
        return MessageDigest.isEqual(
                signature(billet).getBytes(StandardCharsets.US_ASCII),
                signature.getBytes(StandardCharsets.US_ASCII));
    }

    private String signature(Billet billet) {
        String message = billet.getCode() + "|" + billet.getVoyage().getId() + "|" + billet.getSiege();
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(cle, "HmacSHA256"));
            byte[] hmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(Arrays.copyOf(hmac, 16));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HMAC indisponible", e);
        }
    }

    /** @param signature null quand le code a été saisi à la main. */
    public record Contenu(String code, String signature) {
    }
}
