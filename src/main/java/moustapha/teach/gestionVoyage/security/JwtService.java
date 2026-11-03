package moustapha.teach.gestionVoyage.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import moustapha.teach.gestionVoyage.config.AppProperties;
import moustapha.teach.gestionVoyage.domain.Utilisateur;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/** Création et lecture des jetons JWT (HS256). */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationSecondes;

    public JwtService(AppProperties props) {
        this.key = Keys.hmacShaKeyFor(sha256(props.jwt().secret()));
        this.expirationSecondes = props.jwt().expirationMinutes() * 60;
    }

    public String generer(Utilisateur u) {
        Instant maintenant = Instant.now();
        return Jwts.builder()
                .subject(u.getEmail())
                .claim("uid", u.getId())
                .claim("nom", u.getNomComplet())
                .claim("role", u.getRole().name())
                .issuedAt(Date.from(maintenant))
                .expiration(Date.from(maintenant.plusSeconds(expirationSecondes)))
                .signWith(key)
                .compact();
    }

    /** Renvoie l'email (subject) si le jeton est valide et non expiré. */
    public Optional<String> lireSujet(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public long getExpirationSecondes() {
        return expirationSecondes;
    }

    /** Dérive une clé de 256 bits de n'importe quel secret, même court. */
    private static byte[] sha256(String secret) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
