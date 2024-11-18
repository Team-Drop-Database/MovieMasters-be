package movie_master.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents the body of an HTTP request where a user gets registered
 * @param email
 * @param username
 * @param password
 */
public record RegisterUserRequest(@NotNull @Email String email, @NotNull @Size(min = 5) String username, @NotNull @Size(min = 8) String password) {}
