package movie_master.api.controller;

import movie_master.api.jwt.JwtUtil;
import movie_master.api.model.User;
import movie_master.api.model.detail.CustomUserDetails;
import movie_master.api.model.role.Roles;
import movie_master.api.request.LoginRequest;
import movie_master.api.request.RefreshJwtRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {
    private CustomUserDetails userDetails;
    private RefreshJwtRequest refreshJwtRequest;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private UserDetailsService userDetailsService;
    @Mock private Authentication authentication;
    @Mock private BadCredentialsException badCredentialsException;
    @Mock private SignatureException signatureException;
    @InjectMocks private AuthController authController;

    @BeforeEach
    void setup() {
        User user = new User("mock@gmail.com", "mock", "mocked", Roles.ROLE_USER.name(), true);
        userDetails = new CustomUserDetails(user);
        refreshJwtRequest = new RefreshJwtRequest("refreshJwt");
    }

    @Test
    void userCanLogin() {
        LoginRequest loginRequest = new LoginRequest("mock", "mocked");

        Long userId = userDetails.getUserId();
        String username = userDetails.getUsername();
        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("roles", roles);

        String jwt = "jwt";
        String refreshJwt = "refreshJwt";

        Mockito.when(authenticationManager.authenticate(UsernamePasswordAuthenticationToken
                .unauthenticated(loginRequest.username(), loginRequest.password()))).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(jwtUtil.generateJwt(claims, username)).thenReturn(jwt);
        Mockito.when(jwtUtil.generateRefreshJwt(claims, username)).thenReturn(refreshJwt);

        ResponseEntity<Object> result = authController.login(loginRequest);

        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.OK.value()));
        assertEquals(result.getBody(), Map.of("accessToken", jwt, "refreshToken", refreshJwt));
    }

    @Test
    void userCantLoginWithBadCredentials() {
        LoginRequest loginRequest = new LoginRequest("mock", "incorrect_password");

        Mockito.when(authenticationManager.authenticate(UsernamePasswordAuthenticationToken
                .unauthenticated(loginRequest.username(), loginRequest.password()))).thenThrow(badCredentialsException);

        ResponseEntity<Object> result = authController.login(loginRequest);

        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()));
        assertEquals(result.getBody(), badCredentialsException.getMessage());
    }

    @Test
    void userCanRetrieveRefreshToken() {
        Mockito.when(jwtUtil.getSubject(refreshJwtRequest.jwt())).thenReturn(userDetails.getUsername());
        Mockito.when(userDetailsService.loadUserByUsername(jwtUtil.getSubject(refreshJwtRequest.jwt())))
                .thenReturn(userDetails);

        Long userId = userDetails.getUserId();
        String username = userDetails.getUsername();
        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("roles", roles);

        String jwt = "jwt";

        Mockito.when(jwtUtil.isJwtValid(refreshJwtRequest.jwt(), userId, username, roles)).thenReturn(true);
        Mockito.when(jwtUtil.generateJwt(claims, username)).thenReturn(jwt);

        ResponseEntity<Object> result = authController.refresh(refreshJwtRequest);

        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.OK.value()));
        assertEquals(result.getBody(), Map.of("accessToken", jwt));
    }

    @Test
    void userCanHaveInvalidRefreshToken() {
        Mockito.when(jwtUtil.getSubject(refreshJwtRequest.jwt())).thenReturn(userDetails.getUsername());
        Mockito.when(userDetailsService.loadUserByUsername(jwtUtil.getSubject(refreshJwtRequest.jwt()))).thenReturn(userDetails);

        Long userId = userDetails.getUserId();
        String username = userDetails.getUsername();
        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Mockito.when(jwtUtil.isJwtValid(refreshJwtRequest.jwt(), userId, username, roles)).thenReturn(false);

        ResponseEntity<Object> result = authController.refresh(refreshJwtRequest);

        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()));
        assertEquals(result.getBody(), "Invalid refresh token");
    }
}
