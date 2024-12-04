package movie_master.api.jwt;

import movie_master.api.model.User;
import movie_master.api.model.detail.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DefaultJWTUtilTests {
    @Mock DefaultJWTUtil defaultJWTUtil;

    @Test
    void generateJWTSuccessfully() {
        // Given
        User user = new User("mock@gmail.com", "mock","mocked", "USER", true);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        Long userId = userDetails.getUserId();
        String username = userDetails.getUsername();
        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("roles", roles);

        String jwt = "this_is_a_jwt";

        Mockito.when(defaultJWTUtil.generateToken(claims, username)).thenReturn(jwt);
        Mockito.when(defaultJWTUtil.getUserId(jwt)).thenReturn(userId);
        Mockito.when(defaultJWTUtil.getSubject(jwt)).thenReturn(username);

        String generatedJwt = defaultJWTUtil.generateToken(claims, username);

        assertEquals(jwt, generatedJwt);
        assertEquals(userId, defaultJWTUtil.getUserId(jwt));
        assertEquals(username, defaultJWTUtil.getSubject(jwt));
    }

    @Test
    void generatedJWTisValid() {
        // Given
        User user = new User("mock@gmail.com", "mock","mocked", "USER", true);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        Long userId = userDetails.getUserId();
        String username = userDetails.getUsername();
        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("roles", roles);

        String jwt = "this_is_a_jwt";

        Mockito.when(defaultJWTUtil.generateToken(claims, username)).thenReturn(jwt);
        Mockito.when(defaultJWTUtil.getUserId(jwt)).thenReturn(userId);
        Mockito.when(defaultJWTUtil.getSubject(jwt)).thenReturn(username);
        Mockito.when(defaultJWTUtil.isJWTokenValid(jwt, userId, username)).thenReturn(true);

        String generatedJwt = defaultJWTUtil.generateToken(claims, username);

        assertEquals(jwt, generatedJwt);
        assertEquals(userId, defaultJWTUtil.getUserId(jwt));
        assertEquals(username, defaultJWTUtil.getSubject(jwt));
        assertTrue(defaultJWTUtil.isJWTokenValid(jwt, userId, username));
    }

    @Test
    void generatedJWTisInvalid() {
        // Given
        User user = new User("mock@gmail.com", "mock","mocked", "USER", true);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        Long userId = userDetails.getUserId();
        String username = userDetails.getUsername();
        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("roles", roles);

        String jwt = "this_is_a_jwt";

        Mockito.when(defaultJWTUtil.generateToken(claims, username)).thenReturn(jwt);
        Mockito.when(defaultJWTUtil.getUserId(jwt)).thenReturn(userId);
        Mockito.when(defaultJWTUtil.getSubject(jwt)).thenReturn(username);
        Mockito.when(defaultJWTUtil.isJWTokenValid(jwt, userId, username)).thenReturn(false);

        String generatedJwt = defaultJWTUtil.generateToken(claims, username);

        assertEquals(jwt, generatedJwt);
        assertEquals(userId, defaultJWTUtil.getUserId(jwt));
        assertEquals(username, defaultJWTUtil.getSubject(jwt));
        assertFalse(defaultJWTUtil.isJWTokenValid(jwt, userId, username));
    }
}
