package movie_master.api.dto.UserMovie;

import movie_master.api.model.Movie;

public record UserMovieDto(
    long id,
    Movie movie,
    boolean watched,
    UserMovieReviewDto review
) {}
