package movie_master.api.controller;

import movie_master.api.jwt.JwtUtil;
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
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private Authentication authentication;
    @Mock private BadCredentialsException badCredentialsException;
    @Mock private MethodArgumentNotValidException methodArgumentNotValidException;
    @InjectMocks private AuthController authController;

    @Test
    void userCanLogin() {
        User user = new User("mock@gmail.com", "mock","mocked", "USER", true);
        user.setUserId(1L);

        LoginRequest loginRequest = new LoginRequest("mock", "mocked");

        CustomUserDetails userDetails = new CustomUserDetails(user);

        Long userId = userDetails.getUserId();
        String username = userDetails.getUsername();
        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> claims = Map.of("userId", userId, "roles", roles);

        String jwt = "this_is_a_jwt_mock";

        Mockito.when(authenticationManager.authenticate(UsernamePasswordAuthenticationToken
                .unauthenticated(loginRequest.username(), loginRequest.password()))).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(jwtUtil.generateJwt(claims, username)).thenReturn(jwt);

        ResponseEntity<Object> result = authController.login(loginRequest);

        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.OK.value()));
        assertEquals(result.getBody(), jwt);
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
}
