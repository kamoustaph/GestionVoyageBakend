package moustapha.teach.gestionVoyage.securities;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import moustapha.teach.gestionVoyage.entities.User;
import moustapha.teach.gestionVoyage.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private static final long EXPIRATION_TIME = 30 * 60 * 1000; // 30 minutes
    private final String ENCRIPTION_KEY = "608f36e92dc66d97d5933f0e6371493cb4fc05b1aa8f8de64014732472303a7c";
    private final UserService userService;

    public JwtService(UserService userService) {
        this.userService = userService;
    }

    public String generate(String username) {
        try {
            User user = (User) this.userService.loadUserByUsername(username);
            return generateToken(user);
        } catch (Exception e) {
            logger.error("Erreur lors de la génération du token JWT", e);
            throw new JwtException("Erreur de génération du token", e);
        }
    }

    public String extractUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expirationDate = getExpirationDateFromToken(token);
            return expirationDate.before(new Date());
        } catch (JwtException e) {
            logger.error("Token invalide ou expiré", e);
            return true;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (MalformedJwtException e) {
            logger.error("Token JWT malformé: {}", token);
            throw new JwtException("Token JWT invalide", e);
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expiré");
            throw e;
        } catch (Exception e) {
            logger.error("Erreur lors de l'extraction des claims", e);
            throw new JwtException("Erreur de traitement du token", e);
        }
    }

    private String generateToken(User user) {
        final long currentTime = System.currentTimeMillis();

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime + EXPIRATION_TIME))
                .setSubject(user.getUsername())
                .claim("nom", user.getName())
                .claim("roles", roles)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(ENCRIPTION_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}