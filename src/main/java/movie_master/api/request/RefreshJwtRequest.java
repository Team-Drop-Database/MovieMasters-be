package movie_master.api.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Represents the body of an HTTP request where a user tries to retrieve a new jwt
 * @param jwt - jwt
 */
public record RefreshJwtRequest(@NotBlank(message = "Refresh token is required") String jwt) { }
