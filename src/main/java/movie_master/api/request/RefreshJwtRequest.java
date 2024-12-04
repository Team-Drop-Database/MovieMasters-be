package movie_master.api.request;

import jakarta.validation.constraints.NotBlank;

//TODO aparte pull request voor aanmaken
public record RefreshJwtRequest(@NotBlank(message = "Refresh token is required") String jwt) {
}
