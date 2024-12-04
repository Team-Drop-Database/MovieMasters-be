package movie_master.api.jwt;

import io.jsonwebtoken.Claims;

import java.util.Map;

public interface JwtUtil {
    String generateJwt(Map<String, Object> claims, String subject);
    String generateRefreshJwt(Map<String, Object> claims, String subject);
    Claims extractClaims(String token);
    Long getUserId(String jwt);
    String getSubject(String jwt);
    boolean isJwtValid(String jwt, Long userId, String username);
}
