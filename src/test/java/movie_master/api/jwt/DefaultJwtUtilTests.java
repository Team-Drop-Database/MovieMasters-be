package movie_master.api.jwt;

import movie_master.api.model.User;
import movie_master.api.model.detail.CustomUserDetails;
import movie_master.api.model.role.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DefaultJwtUtilTests {
    private Long userId;
    private String username;
    private Role role;
    private Map<String, Object> claims;
    private String jwt;
    private String refreshJwt;
    @Mock
    DefaultJwtUtil defaultJWTUtil;

    @BeforeEach
    void setup() {
        User user = new User("mock@gmail.com", "mock", "mocked", Role.ROLE_USER, true);
        user.setUserId(1L);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        userId = userDetails.getUserId();
        username = userDetails.getUsername();
        role = userDetails.getRole();
        claims = Map.of("userId", userId, "roles", role);
        jwt = "jwt";
        refreshJwt = "refreshJwt";
    }

    @Test
    void canGenerateJwt() {
        Mockito.when(defaultJWTUtil.generateJwt(claims, username)).thenReturn(jwt);

        String jwt = defaultJWTUtil.generateJwt(claims, username);

        Mockito.when(defaultJWTUtil.getUserId(jwt)).thenReturn(userId);
        Mockito.when(defaultJWTUtil.getSubject(jwt)).thenReturn(username);
        Mockito.when(defaultJWTUtil.isJwtValid(jwt, userId, username, role)).thenReturn(true);

        assertNotNull(jwt);
        assertEquals(userId, defaultJWTUtil.getUserId(jwt));
        assertEquals(username, defaultJWTUtil.getSubject(jwt));
        assertTrue(defaultJWTUtil.isJwtValid(jwt, userId, username, role));
    }

    @Test
    void generatedJwtCanBeInvalid() {
        Mockito.when(defaultJWTUtil.generateJwt(claims, username)).thenReturn(jwt);

        String jwt = defaultJWTUtil.generateJwt(claims, username);

        Mockito.when(defaultJWTUtil.getUserId(jwt)).thenReturn(userId);
        Mockito.when(defaultJWTUtil.getSubject(jwt)).thenReturn(username);
        Mockito.when(defaultJWTUtil.isJwtValid(jwt, userId, username, role)).thenReturn(false);

        assertNotNull(jwt);
        assertEquals(userId, defaultJWTUtil.getUserId(jwt));
        assertEquals(username, defaultJWTUtil.getSubject(jwt));
        assertFalse(defaultJWTUtil.isJwtValid(jwt, userId, username, role));
    }

    @Test
    void canGenerateRefreshJwt() {
        Mockito.when(defaultJWTUtil.generateRefreshJwt(claims, username)).thenReturn(refreshJwt);

        String refreshJwt = defaultJWTUtil.generateRefreshJwt(claims, username);

        Mockito.when(defaultJWTUtil.getUserId(refreshJwt)).thenReturn(userId);
        Mockito.when(defaultJWTUtil.getSubject(refreshJwt)).thenReturn(username);
        Mockito.when(defaultJWTUtil.isJwtValid(refreshJwt, userId, username, role)).thenReturn(true);

        assertNotNull(refreshJwt);
        assertEquals(userId, defaultJWTUtil.getUserId(refreshJwt));
        assertEquals(username, defaultJWTUtil.getSubject(refreshJwt));
        assertTrue(defaultJWTUtil.isJwtValid(refreshJwt, userId, username, role));
    }

    @Test
    void generatedRefreshJwtCanBeInvalid() {
        Mockito.when(defaultJWTUtil.generateRefreshJwt(claims, username)).thenReturn(refreshJwt);

        String jwt = defaultJWTUtil.generateRefreshJwt(claims, username);

        Mockito.when(defaultJWTUtil.getUserId(jwt)).thenReturn(userId);
        Mockito.when(defaultJWTUtil.getSubject(jwt)).thenReturn(username);
        Mockito.when(defaultJWTUtil.isJwtValid(jwt, userId, username, role)).thenReturn(false);

        assertNotNull(jwt);
        assertEquals(userId, defaultJWTUtil.getUserId(jwt));
        assertEquals(username, defaultJWTUtil.getSubject(jwt));
        assertFalse(defaultJWTUtil.isJwtValid(jwt, userId, username, role));
    }
}
