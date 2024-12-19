package movie_master.api.dto.UserMovie;

import java.time.LocalDateTime;

public record UserMovieReviewDto(
    long reviewId,
    double rating,
    String comment,
    LocalDateTime reviewDate
) {}
