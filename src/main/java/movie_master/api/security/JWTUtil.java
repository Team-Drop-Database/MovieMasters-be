package movie_master.api.security;

import io.jsonwebtoken.Claims;

import java.util.Map;

public interface JWTUtil {
    String generateToken(Map<String, Object> claims, String subject);
    Claims extractClaims(String token);
    Long extractUserId(String token);
    String extractSubject(String token);
    boolean isJWTokenValid(String token, String username);
    boolean isJWTokenExpired(String token);
}
