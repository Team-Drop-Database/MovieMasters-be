package movie_master.api.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshJwtRequest(@NotBlank(message = "Refresh token is required") String jwt) { }
