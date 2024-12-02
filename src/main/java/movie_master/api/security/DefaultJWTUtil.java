package movie_master.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class DefaultJWTUtil implements JWTUtil {
    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(Map<String, Object> claims, String subject) {
        Key key = getSigningKey();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5))  // 5 hours
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        Key key = getSigningKey();

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Helper method to get the signing key from the base64-encoded secret
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    @Override
    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    @Override
    public boolean isJWTokenValid(String token, String username) {
        final String subject = extractSubject(token);
        return (subject.equals(username) && !isJWTokenExpired(token));
    }

    @Override
    public boolean isJWTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}