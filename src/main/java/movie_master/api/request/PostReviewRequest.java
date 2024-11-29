package movie_master.api.request;

import jakarta.validation.constraints.NotNull;

public record PostReviewRequest(
    @NotNull long userId,
    @NotNull long movieId,
    @NotNull double rating,
    @NotNull String comment
) {}
