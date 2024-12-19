package movie_master.api.mapper;

import movie_master.api.dto.UserMovie.UserMovieDto;
import movie_master.api.dto.UserMovie.UserMovieReviewDto;
import movie_master.api.model.Review;
import movie_master.api.model.UserMovie;
import org.springframework.stereotype.Service;

@Service
public class UserMovieDtoMapper {

    public UserMovieDto mapUserMovieToDto(UserMovie userMovie) {
        return new UserMovieDto(
            userMovie.getId(),
            userMovie.getMovie(),
            userMovie.isWatched(),
            mapReviewToUserMovieReviewDto(userMovie.getReview())
        );
    }

    private UserMovieReviewDto mapReviewToUserMovieReviewDto(Review review) {
        if (review == null) return null;
        return new UserMovieReviewDto(
            review.getReviewId(),
            review.getRating(),
            review.getComment(),
            review.getReviewDate()
        );
    }
}
