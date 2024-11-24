package movie_master.api.controller;

import movie_master.api.security.JwTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwTUtil jwtUtil;

    public AuthController(JwTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> generateToken(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        Map<String, Object> claims = Map.of("role", "USER"); // Example claims
        String token = jwtUtil.generateToken(claims, username);
        return ResponseEntity.ok(token);
    }
}