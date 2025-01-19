package movie_master.api.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(
        @NotBlank(message = "Please enter the email that you use for signing in") String email
) {
}
