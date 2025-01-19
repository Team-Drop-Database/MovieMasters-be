package movie_master.api.jwt;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import movie_master.api.model.detail.CustomUserDetails;
import movie_master.api.model.role.Role;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Custom filter for JWT
 * This class is a filter that has the responsibility to validate the jwt of a user
 * If the jwt is valid, Spring sets the user performing the request as the currently authenticated user and
 * the user gets access to the protected resource since the jwt has passed the filter
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Requests to: POST /users, /auth/** and GET /reviews should not be filtered
     * Meaning: a jwt is not required
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String servletPath = request.getServletPath();

        return ((servletPath.equals("/users") && request.getMethod().equals("POST")) ||
                (servletPath.equals("/users/password-reset")) || (servletPath.startsWith("/auth"))
                || (servletPath.startsWith("/reviews") && request.getMethod().equals("GET"))
                || (servletPath.startsWith("/movies") && request.getMethod().equals("GET")));
    }

    /**
     * Requests to other endpoints should be filtered
     * A user needs to have a valid jwt in order to access protected resources
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = request.getHeader("Authorization");

        // There is no authorization header
        if (jwt == null || !jwt.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Authentication is required to access this resource\"}");
            return;
        }

        // Remove "Bearer " prefix
        jwt = jwt.substring(7);

        try {
            // Extract the user id from the jwt
            Long jwtUserId = jwtUtil.getUserId(jwt);
            // Extract the username from the jwt
            String jwtUsername = jwtUtil.getSubject(jwt);

            // The userId and username can not be null. Also at this point, Spring should not hold a reference to an authenticated user yet
            if (jwtUserId != null && jwtUsername != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Get the details of the user from the database
                CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(jwtUsername);
                Long userId = userDetails.getUserId();
                String username = userDetails.getUsername();
                Role role = userDetails.getRole();

                // Check if the jwt is valid
                if (jwtUtil.isJwtValid(jwt, userId, username, role)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    // Set the details about the authenticated request
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Store the details of the user who is currently authenticated
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(usernamePasswordAuthenticationToken);
                    SecurityContextHolder.setContext(context);
                }
            }
        }
        catch (SignatureException | MalformedJwtException | UnsupportedJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid or expired jwt\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}