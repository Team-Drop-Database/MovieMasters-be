package movie_master.api.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Represents the body of an HTTP request where a user tries to log in
 * @param username - the username
 * @param password - the password
 */
public record LoginRequest(@NotBlank(message = "Please enter a username") String username,
                           @NotBlank(message = "Please enter a password") String password) {
}
