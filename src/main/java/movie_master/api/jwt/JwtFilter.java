package movie_master.api.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import movie_master.api.model.detail.CustomUserDetails;
import movie_master.api.service.CustomUserDetailsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Custom filter for JWT
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String servletPath = request.getServletPath();

        return (servletPath.equals("/users") && request.getMethod().equals("POST")) || (servletPath.startsWith("/auth"));
    }

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
            Long userId = jwtUtil.getUserId(jwt);
            String username = jwtUtil.getSubject(jwt);

            if (userId != null && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

                // check if the jwt is valid
                if (jwtUtil.isJwtValid(jwt, userDetails.getUserId(), userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Store the details of the user who is currently authenticated
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(usernamePasswordAuthenticationToken);
                    SecurityContextHolder.setContext(context);
                }
            }
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid or expired jwt\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}