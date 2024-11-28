package movie_master.api.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank(message = "Please enter a username") String username,
                           @NotBlank(message = "Please enter a password") String password) {
}
