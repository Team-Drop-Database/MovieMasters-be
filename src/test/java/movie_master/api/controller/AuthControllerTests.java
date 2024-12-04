package movie_master.api.controller;

import movie_master.api.jwt.DefaultJWTUtil;
import movie_master.api.model.User;
import movie_master.api.model.detail.CustomUserDetails;
import movie_master.api.request.LoginRequest;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {
    @Mock private AuthenticationManager authenticationManager;
    @Mock private DefaultJWTUtil jwtUtil;
    @InjectMocks private AuthController authController;

    @Test
    void loginSuccessfully() {
        // Given
        User user = new User("mock@gmail.com", "mock","mocked", "USER", true);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        LoginRequest loginRequest = new LoginRequest("mock", "mocked");

        Authentication authentication = Mockito.mock(Authentication.class);

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

        String jwt = "this_is_a_jwt_mock";

        Mockito.when(authenticationManager.authenticate(UsernamePasswordAuthenticationToken
                .unauthenticated(loginRequest.username(), loginRequest.password()))).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(jwtUtil.generateToken(claims, username)).thenReturn(jwt);

        // When
        ResponseEntity<Object> result = authController.login(loginRequest);

        // Then
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.OK.value()));
        assertEquals(result.getBody(), jwt);
    }

    @Test
    void loginFailed() {
        // Given
        LoginRequest loginRequest = new LoginRequest("mock", "mocked");

        BadCredentialsException badCredentialsException = Mockito.mock(BadCredentialsException.class);

        Mockito.when(authenticationManager.authenticate(UsernamePasswordAuthenticationToken
                .unauthenticated(loginRequest.username(), loginRequest.password()))).thenThrow(badCredentialsException);

        // When
        ResponseEntity<Object> result = authController.login(loginRequest);

        // Then
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()));
        assertEquals(result.getBody(), badCredentialsException.getMessage());
    }
}
