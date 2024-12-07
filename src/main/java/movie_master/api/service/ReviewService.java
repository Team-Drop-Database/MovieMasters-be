package movie_master.api.service;

import movie_master.api.dto.ReviewDto;
import movie_master.api.exception.MovieNotInWatchlistException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.mapper.ReviewDtoMapper;
import movie_master.api.model.Review;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.repository.ReviewRepository;
import movie_master.api.repository.UserRepository;
import movie_master.api.request.PostReviewRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Review service
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewDtoMapper mapper;

    public ReviewService(
        ReviewRepository reviewRepository,
        UserRepository userRepository,
        ReviewDtoMapper mapper
    ) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
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

    // Create a review
    public ReviewDto postReview(PostReviewRequest reviewRequest)
            throws UserNotFoundException, MovieNotInWatchlistException {

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

        Review reviewToStore = new Review(
            userMovie.get(),
            reviewRequest.rating(),
            reviewRequest.comment()
        );

        Review storedReview = reviewRepository.save(reviewToStore);

        return mapper.mapToDTO(storedReview);
    }
}
