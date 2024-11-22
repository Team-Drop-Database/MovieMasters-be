package movie_master.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents the body of an HTTP request where a user gets registered
 * @param email
 * @param username
 * @param password
 */
public record RegisterUserRequest(@NotBlank(message = "Please enter an email") @Email(message = "Invalid email") String email,
                                  @NotNull(message = "Username can't be null") @Size(min = 5, message = "Username needs to be at least 5 characters long") String username,
                                  @NotNull(message = "Password can't be null") @Size(min = 8, message = "Password needs to be at least 8 characters long") String password) {}
