package movie_master.api.jwt;

import movie_master.api.model.role.Role;

import java.util.Map;

public interface JwtUtil {
    String generateJwt(Map<String, Object> claims, String subject);
    String generateRefreshJwt(Map<String, Object> claims, String subject);
    Object extractClaims(String token);
    Long getUserId(String jwt);
    String getSubject(String jwt);
    Role getRole(String jwt);
    boolean isJwtValid(String jwt, Long userId, String username, Role role);
}
