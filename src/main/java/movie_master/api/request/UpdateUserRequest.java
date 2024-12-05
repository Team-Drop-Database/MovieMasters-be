package movie_master.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest (
        @NotNull(message = "Username can't be null") @Size(min = 5, message = "Username needs to be at least 5 characters long")  String username,
        @NotBlank(message = "Please enter an email") @Email(message = "Invalid email")  String email,
        String profilePicture
) {}
