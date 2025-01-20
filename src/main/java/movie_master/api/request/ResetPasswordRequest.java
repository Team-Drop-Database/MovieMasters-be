package movie_master.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Please enter your password reset token") String passwordResetToken,
        @NotBlank(message = "Please enter your new password")
        @Size(min = 8, message = "Password needs to be at least 8 characters long")
        String newPassword
) {
}
