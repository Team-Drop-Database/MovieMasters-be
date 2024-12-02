package movie_master.api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * The default JWT Util implementation
 */
@Component
public class DefaultJWTUtil implements JWTUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Override
    public String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5)) // 5 hours validity
                .signWith(SignatureAlgorithm.HS256, this.secret)
                .compact();
    }

    @Override
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(this.secret)
                .parseClaimsJws(token)
                .getBody();
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