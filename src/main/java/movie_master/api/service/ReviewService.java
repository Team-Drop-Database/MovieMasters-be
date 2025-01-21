package movie_master.api.service;

import jakarta.transaction.Transactional;
import movie_master.api.dto.ReviewDto;
import movie_master.api.exception.MovieNotFoundException;
import movie_master.api.exception.MovieNotInWatchlistException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.mapper.ReviewDtoMapper;
import movie_master.api.model.Movie;
import movie_master.api.model.Review;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.repository.MovieRepository;
import movie_master.api.repository.ReviewRepository;
import movie_master.api.repository.UserMovieRepository;
import movie_master.api.repository.UserRepository;
import movie_master.api.request.PostReviewRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Review service
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final UserMovieRepository userMovieRepository;
    private final MovieRepository movieRepository;
    private final ReviewDtoMapper mapper;

    public ReviewService(
        ReviewRepository reviewRepository,
        UserRepository userRepository,
        UserMovieRepository userMovieRepository,
        MovieRepository movieRepository,
        ReviewDtoMapper mapper
    ) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.userMovieRepository = userMovieRepository;
        this.movieRepository = movieRepository;
        this.mapper = mapper;
    }

    // Retrieve all reviews
    public List<ReviewDto> findAll() {
        return reviewRepository.findAll()
            .stream()
            .map(mapper::mapToDTO)
            .toList();
    }

    // Retrieve a certain amount of reviews
    public List<ReviewDto> findByAmount(int maxAmount) {
        return reviewRepository.findAll()
            .stream()
            .limit(maxAmount)
            .map(mapper::mapToDTO)
            .toList();
    }

    // Retrieve a list of reviews by movie
    public List<ReviewDto> findByMovie(long id) {
        return userMovieRepository
            .findReviewsByMovieId(id)
            .stream()
            .filter(Objects::nonNull)
            .map(mapper::mapToDTO)
            .toList();
    }

    // Create a review
    public ReviewDto postReview(PostReviewRequest reviewRequest)
            throws UserNotFoundException, MovieNotInWatchlistException, MovieNotFoundException {

        Optional<User> foundUser = userRepository.findById(reviewRequest.userId());

        // Throw an exception if the user has not been found
        if (foundUser.isEmpty()) {
            throw new UserNotFoundException(reviewRequest.userId());
        }

        User user = foundUser.get();
        // Get the watchlist of the found user
        Set<UserMovie> watchlist = user.getWatchList();

        // Get the movie from your watchlist that you want to place a review on
        Optional<UserMovie> userMovie = watchlist.stream()
            .filter(item -> item.getMovie().getId() == reviewRequest.movieId())
            .findFirst();

        // Throw an exception if the movie that you want to place a review on has not been found
        if (userMovie.isEmpty()) {
            throw new MovieNotInWatchlistException(reviewRequest.movieId(), reviewRequest.userId());
        }
        UserMovie foundUserMovie = userMovie.get();

        Review reviewToStore;
        if (foundUserMovie.getReview() == null) {
            // If the user has not reviewed movie yet, create new
            reviewToStore = new Review(
                foundUserMovie,
                reviewRequest.rating(),
                reviewRequest.comment()
            );
        } else {
            // Else update existing
            reviewToStore = foundUserMovie.getReview();
            reviewToStore.setRating(reviewRequest.rating());
            reviewToStore.setComment(reviewRequest.comment());
        }

        Review storedReview = reviewRepository.save(reviewToStore);
        foundUserMovie.setReview(storedReview);
        userMovieRepository.save(foundUserMovie);
        updateMovieAverageRating(reviewRequest.movieId());

        return mapper.mapToDTO(storedReview);
    }

    @Transactional
    public void deleteReview(long reviewId) throws MovieNotFoundException {
        // Find the review and its associated UserMovie
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        UserMovie userMovie = review.getUserMovie();

        // Remove the review from the UserMovie
        userMovie.setReview(null);
        userMovieRepository.save(userMovie);

        // Delete the review
        reviewRepository.deleteById(reviewId);
        updateMovieAverageRating(review.getUserMovie().getMovie().getId());
    }

    private void updateMovieAverageRating(long movieId) throws MovieNotFoundException {
        List<Review> reviews = userMovieRepository.findReviewsByMovieId(movieId);
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));

        double avgRating = reviews.stream()
                .filter(Objects::nonNull)
                .mapToDouble(Review::getRating)
                .average()
                .orElse(Double.NaN);

        movie.setMmAvgRating(Double.isNaN(avgRating) ? null : avgRating);
        movieRepository.save(movie);
    }
}
