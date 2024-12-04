package movie_master.api.jwt;

import io.jsonwebtoken.Claims;

import java.util.Map;

public interface JWTUtil {
    String generateToken(Map<String, Object> claims, String subject);
    String generateRefreshToken(Map<String, Object> claims, String subject);
    Long getUserId(String jwt);
    String getSubject(String jwt);
    Claims extractClaims(String token);
    boolean isJWTokenValid(String jwt, Long userId, String username);
}
