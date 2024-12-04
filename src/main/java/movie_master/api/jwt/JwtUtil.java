package movie_master.api.jwt;

import java.util.Map;

public interface JwtUtil {
    String generateJwt(Map<String, Object> claims, String subject);
    Long getUserId(String jwt);
    String getSubject(String jwt);
    boolean isJwtValid(String jwt, Long userId, String username);
}
