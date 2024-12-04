package movie_master.api.jwt;

import movie_master.api.model.User;
import movie_master.api.model.detail.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DefaultJwtUtilTests {
    @Mock
    DefaultJwtUtil defaultJWTUtil;

    @Test
    void canGenerateJwt() {
        User user = new User("mock@gmail.com", "mock","mocked", "USER", true);
        user.setUserId(1L);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        Long userId = userDetails.getUserId();
        String username = userDetails.getUsername();
        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> claims = Map.of("userId", userId, "roles", roles);

        Mockito.when(defaultJWTUtil.generateJwt(claims, username)).thenReturn("this_is_the_jwt");

        String jwt = defaultJWTUtil.generateJwt(claims, username);

        Mockito.when(defaultJWTUtil.getUserId(jwt)).thenReturn(userId);
        Mockito.when(defaultJWTUtil.getSubject(jwt)).thenReturn(username);
        Mockito.when(defaultJWTUtil.isJwtValid(jwt, userId, username)).thenReturn(true);

        assertNotNull(jwt);
        assertEquals(userId, defaultJWTUtil.getUserId(jwt));
        assertEquals(username, defaultJWTUtil.getSubject(jwt));
        assertTrue(defaultJWTUtil.isJwtValid(jwt, userId, username));
    }

    @Test
    void generatedJwtCanBeInvalid() {
        // Given
        User user = new User("mock@gmail.com", "mock","mocked", "USER", true);
        user.setUserId(1L);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        Long userId = userDetails.getUserId();
        String username = userDetails.getUsername();
        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> claims = Map.of("userId", userId, "roles", roles);

        Mockito.when(defaultJWTUtil.generateJwt(claims, username)).thenReturn("this_is_the_jwt");

        String jwt = defaultJWTUtil.generateJwt(claims, username);

        Mockito.when(defaultJWTUtil.getUserId(jwt)).thenReturn(userId);
        Mockito.when(defaultJWTUtil.getSubject(jwt)).thenReturn(username);
        Mockito.when(defaultJWTUtil.isJwtValid(jwt, userId, username)).thenReturn(false);

        assertNotNull(jwt);
        assertEquals(userId, defaultJWTUtil.getUserId(jwt));
        assertEquals(username, defaultJWTUtil.getSubject(jwt));
        assertFalse(defaultJWTUtil.isJwtValid(jwt, userId, username));
    }

    @Test
    void canGenerateRefreshJwt() {
        User user = new User("mock@gmail.com", "mock","mocked", "USER", true);
        user.setUserId(1L);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        Long userId = userDetails.getUserId();
        String username = userDetails.getUsername();
        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> claims = Map.of("userId", userId, "roles", roles);

        Mockito.when(defaultJWTUtil.generateRefreshJwt(claims, username)).thenReturn("this_is_the_refresh_jwt");

        String refreshJwt = defaultJWTUtil.generateRefreshJwt(claims, username);

        Mockito.when(defaultJWTUtil.getUserId(refreshJwt)).thenReturn(userId);
        Mockito.when(defaultJWTUtil.getSubject(refreshJwt)).thenReturn(username);
        Mockito.when(defaultJWTUtil.isJwtValid(refreshJwt, userId, username)).thenReturn(true);

        assertNotNull(refreshJwt);
        assertEquals(userId, defaultJWTUtil.getUserId(refreshJwt));
        assertEquals(username, defaultJWTUtil.getSubject(refreshJwt));
        assertTrue(defaultJWTUtil.isJwtValid(refreshJwt, userId, username));
    }

    @Test
    void generatedRefreshJwtCanBeInvalid() {
        User user = new User("mock@gmail.com", "mock","mocked", "USER", true);
        user.setUserId(1L);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        Long userId = userDetails.getUserId();
        String username = userDetails.getUsername();
        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> claims = Map.of("userId", userId, "roles", roles);

        Mockito.when(defaultJWTUtil.generateRefreshJwt(claims, username)).thenReturn("this_is_the_refresh_jwt");

        String jwt = defaultJWTUtil.generateRefreshJwt(claims, username);

        Mockito.when(defaultJWTUtil.getUserId(jwt)).thenReturn(userId);
        Mockito.when(defaultJWTUtil.getSubject(jwt)).thenReturn(username);
        Mockito.when(defaultJWTUtil.isJwtValid(jwt, userId, username)).thenReturn(false);

        assertNotNull(jwt);
        assertEquals(userId, defaultJWTUtil.getUserId(jwt));
        assertEquals(username, defaultJWTUtil.getSubject(jwt));
        assertFalse(defaultJWTUtil.isJwtValid(jwt, userId, username));
    }
}
