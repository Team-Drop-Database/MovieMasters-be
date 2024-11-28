package movie_master.api.controller;

import jakarta.validation.Valid;
import movie_master.api.request.LoginRequest;
import movie_master.api.security.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public AuthenticationController(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    //TODO multiple roles
    //TODO security context
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password()));

        if (authentication.isAuthenticated()) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);

            logger.info(authentication.getName());
            logger.info(authentication.getPrincipal().toString());
            logger.info(context.getAuthentication().toString());

            Map<String, Object> roles = authentication
                    .getAuthorities()
                    .stream()
                    .collect(Collectors.toMap( role -> "role", Object::toString)); // Value mapper ));

            String jwt = jwtUtil.generateToken(roles, authentication.getName());
            return ResponseEntity.ok().body(jwt);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}