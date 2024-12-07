package movie_master.api.jwt;

import java.util.List;
import java.util.Map;

public interface JwtUtil {
    String generateJwt(Map<String, Object> claims, String subject);
    String generateRefreshJwt(Map<String, Object> claims, String subject);
    Object extractClaims(String token);
    Long getUserId(String jwt);
    String getSubject(String jwt);
    List<String> getRoles(String jwt);
    boolean isJwtValid(String jwt, Long userId, String username, List<String> roles);
}
