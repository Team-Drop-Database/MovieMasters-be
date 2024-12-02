package movie_master.api.jwt;

import java.util.Map;

public interface JWTUtil {
    String generateToken(Map<String, Object> claims, String subject);
    Long getUserId(String jwt);
    String getSubject(String jwt);
    boolean isJWTokenValid(String jwt, Long userId, String username);
}
