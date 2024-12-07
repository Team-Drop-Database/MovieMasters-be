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
 * The purpose of this class is to generate JSON web tokens and to extract meaningful information from them
 * These web tokens are being used for authorization
 * They contain some meaningful data about a user such as: their id, their username and their given roles within the application
 * With this data, the application knows to which resources a user should have access to
 */
@Component
public class DefaultJwtUtil implements JwtUtil {
    /**
     * This is a secret string value that only the back-end application should have access to
     * It is being used alongside a hashing algorithm such as HS256 to sign a jwt
     * By signing the jwt, the integrity of the jwt is being assured
     */
    @Value("${jwt.secret}")
    private String secret;

    // Generate a jwt for a user
    @Override
    public String generateJwt(Map<String, Object> claims, String subject) {
        Key key = getSigningKey();

        return Jwts.builder()
                .setClaims(claims) // set the statements about the user
                .setSubject(subject) // set the username
                .setIssuedAt(new Date()) // set the date when the jwt has been created
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))  // jwt is valid for 1 day
                .signWith(key, SignatureAlgorithm.HS256) // sign the jwt with a secret key and hashing algorithm hs256
                .compact();
    }

    // Generate a refresh jwt for a user
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

    /**
     * Extract the statements about a user
     * e.g. their: id, username and roles
     */
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

    // Extract the id of a user
    @Override
    public Long getUserId(String jwt) {
        return extractClaims(jwt).get("userId", Long.class);
    }

    // Extract the username of a user
    @Override
    public String getSubject(String jwt) {
        return extractClaims(jwt).getSubject();
    }

    // Extract the given roles of a user
    @Override
    public List<String> getRoles(String jwt) {
        return extractClaims(jwt).get("roles", List.class);
    }

    // Check whether the jwt is valid
    @Override
    public boolean isJwtValid(String jwt, Long userId, String username, List<String> roles) {
        return (getUserId(jwt).equals(userId) && getSubject(jwt).equals(username)
                && getRoles(jwt).equals(roles) && !isJwtExpired(jwt));
    }

    // Helper method to check whether the jwt is valid
    private boolean isJwtExpired(String jwt) {
        return extractClaims(jwt).getExpiration().before(new Date());
    }
}