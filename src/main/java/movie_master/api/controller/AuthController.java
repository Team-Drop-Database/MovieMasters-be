package movie_master.api.controller;

import jakarta.validation.Valid;
import movie_master.api.model.detail.CustomUserDetails;
import movie_master.api.request.LoginRequest;
import movie_master.api.security.DefaultJWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Authentication controller
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final DefaultJWTUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, DefaultJWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authenticationRequest = this.authenticationManager
                    .authenticate(UsernamePasswordAuthenticationToken
                            .unauthenticated(loginRequest.username(), loginRequest.password()));

            // get the details of the user such as: id, username etc
            CustomUserDetails userDetails = (CustomUserDetails) authenticationRequest.getPrincipal();

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

            String jwt = jwtUtil.generateToken(claims, username);

            return ResponseEntity.ok().body(jwt);
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}