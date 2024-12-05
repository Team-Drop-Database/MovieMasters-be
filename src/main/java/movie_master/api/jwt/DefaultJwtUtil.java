package movie_master.api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The default JWT Util implementation
 */
@Component
public class DefaultJwtUtil implements JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Override
    public String generateJwt(Map<String, Object> claims, String subject) {
        Key key = getSigningKey();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))  // 1 day
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshJwt(Map<String, Object> claims, String subject) {
        Key key = getSigningKey();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 3))  // 3 days
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
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
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Long getUserId(String jwt) {
        return extractClaims(jwt).get("userId", Long.class);
    }

    @Override
    public String getSubject(String jwt) {
        return extractClaims(jwt).getSubject();
    }

    @Override
    public List<String> getRoles(String jwt) {
        return extractClaims(jwt).get("roles", List.class);
    }

    @Override
    public boolean isJwtValid(String jwt, Long userId, String username, List<String> roles) {
        return (getUserId(jwt).equals(userId) && getSubject(jwt).equals(username)
                && getRoles(jwt).equals(roles) && !isJwtExpired(jwt));
    }

    private boolean isJwtExpired(String jwt) {
        return extractClaims(jwt).getExpiration().before(new Date());
    }
}