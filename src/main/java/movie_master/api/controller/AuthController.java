package movie_master.api.controller;

import jakarta.validation.Valid;
import movie_master.api.jwt.JwtUtil;
import movie_master.api.model.detail.CustomUserDetails;
import movie_master.api.request.LoginRequest;
import movie_master.api.request.RefreshJwtRequest;
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

/**
 * Authentication controller
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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
            List<String> roles = userDetails
                    .getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            Map<String, Object> claims = Map.of("userId", userId, "roles", roles);

            String jwt = jwtUtil.generateJwt(claims, username);
            String refreshToken = jwtUtil.generateRefreshJwt(claims, username);

            return ResponseEntity.ok().body(Map.of("accessToken", jwt, "refreshToken", refreshToken));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


    //TODO APARTE pull request aanmaken
//    @PostMapping("/refresh")
//    public ResponseEntity<Object> refresh(@Valid @RequestBody RefreshJwtRequest refreshJwtRequest) {
//        try {
//            String username = jwtUtil.getSubject(refreshJwtRequest.jwt());
//            Long userId = jwtUtil.getUserId(refreshJwtRequest.jwt());
//
//            if (!jwtUtil.isJwtValid(refreshJwtRequest.jwt(), userId, username)) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
//            }
//
//            // Generate new JWT
//            Map<String, Object> claims = new HashMap<>();
//            claims.put("userId", userId);
//            claims.put("roles", jwtUtil.extractClaims(refreshJwtRequest.jwt()).get("roles"));
//
//            String newAccessToken = jwtUtil.generateJwt(claims, username);
//
//            return ResponseEntity.ok().body(Map.of("accessToken", newAccessToken));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired or invalid");
//        }
//    }

}