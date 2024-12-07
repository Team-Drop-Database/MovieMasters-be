package movie_master.api.controller;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.Valid;
import movie_master.api.jwt.JwtUtil;
import movie_master.api.model.detail.CustomUserDetails;
import movie_master.api.model.role.Role;
import movie_master.api.request.LoginRequest;
import movie_master.api.request.RefreshJwtRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication controller
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authenticationRequest = this.authenticationManager
                    .authenticate(UsernamePasswordAuthenticationToken
                            .unauthenticated(loginRequest.username(), loginRequest.password()));

            CustomUserDetails userDetails = (CustomUserDetails) authenticationRequest.getPrincipal();

            Long userId = userDetails.getUserId();
            String username = userDetails.getUsername();
            Role role = userDetails.getRole();
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userId);
            claims.put("role", role);

            String jwt = jwtUtil.generateJwt(claims, username);
            String refreshJwt = jwtUtil.generateRefreshJwt(claims, username);

            return ResponseEntity.ok().body(Map.of("accessToken", jwt, "refreshToken", refreshJwt));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Generate a new JWT
     * @param refreshJwtRequest - the refresh token
     * @return - newly generated jwt
     */
    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@Valid @RequestBody RefreshJwtRequest refreshJwtRequest) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService
                    .loadUserByUsername(jwtUtil.getSubject(refreshJwtRequest.jwt()));

            // get the details of the user from the database
            Long userId = userDetails.getUserId();
            String username = userDetails.getUsername();
            Role role = userDetails.getRole();

            // validate the claims and if the token has not expired
            if (!jwtUtil.isJwtValid(refreshJwtRequest.jwt(), userId, username, role)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }

            // Generate new JWT
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userId);
            claims.put("role", role);

            String jwt = jwtUtil.generateJwt(claims, username);

            return ResponseEntity.ok().body(Map.of("accessToken", jwt));
        }
        catch (SignatureException | MalformedJwtException | UnsupportedJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired or invalid");
        }
    }
}