package movie_master.api.service;

import movie_master.api.dto.ReviewDto;
import movie_master.api.exception.MovieNotInWatchlistException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.mapper.ReviewDtoMapper;
import movie_master.api.model.Movie;
import movie_master.api.model.Review;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.model.role.Role;
import movie_master.api.repository.ReviewRepository;
import movie_master.api.repository.UserMovieRepository;
import movie_master.api.repository.UserRepository;
import movie_master.api.request.PostReviewRequest;
import movie_master.utils.TestUtils;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static movie_master.utils.TestUtils.createRandomRecord;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserMovieRepository userMovieRepository;
    @Mock private ReviewDtoMapper mapper;
    @InjectMocks private ReviewService reviewService;

    EasyRandom easyRandom = new EasyRandom();

    @Test
    void canFindAll() {
        // Given
        int expectedAmount = 100;
        List<Review> storedReviews = easyRandom.objects(Review.class, expectedAmount).toList();
        ReviewDto mapped = createRandomRecord(ReviewDto.class, easyRandom);
        ArrayList<ReviewDto> expectedResult = new ArrayList<>();
        for (int i = 0; i < expectedAmount; i++) {
            expectedResult.add(mapped);
        }

        Mockito.when(reviewRepository.findAll()).thenReturn(storedReviews);
        Mockito.when(mapper.mapToDTO(Mockito.any())).thenReturn(mapped);

        // When
        List<ReviewDto> result = reviewService.findAll();

        // Then
        assertEquals(result, expectedResult.stream().toList());
    }

    @Test
    void canFindByAmount() {
        // Given
        int desiredAmount = 5;
        int actualAmount = 10;
        List<Review> storedReviews = easyRandom.objects(Review.class, actualAmount).toList();
        ReviewDto mapped = createRandomRecord(ReviewDto.class, easyRandom);
        ArrayList<ReviewDto> expectedResult = new ArrayList<>();
        for (int i = 0; i < desiredAmount; i++) {
            expectedResult.add(mapped);
        }

        Mockito.when(reviewRepository.findAll()).thenReturn(storedReviews);
        Mockito.when(mapper.mapToDTO(Mockito.any())).thenReturn(mapped);

        // When
        List<ReviewDto> result = reviewService.findByAmount(desiredAmount);

        // Then
        assertEquals(result, expectedResult.stream().toList());
    }

    @Test
    void canFindByMovie() {
        // Arrange
        int streamSize = 10;
        long id = easyRandom.nextLong();
        List<Review> stored = easyRandom.objects(Review.class, streamSize).toList();
        List<ReviewDto> mapped = TestUtils.createMultipleRandomRecords(ReviewDto.class, easyRandom, streamSize);

        Mockito.when(userMovieRepository.findReviewsByMovieId(id)).thenReturn(stored);
        for (int i = 0; i < streamSize; i++) {
            Mockito.when(mapper.mapToDTO(stored.get(i))).thenReturn(mapped.get(i));
        }

        // Act
        List<ReviewDto> result = reviewService.findByMovie(id);

        // Assert
        assertEquals(mapped, result);
    }

    @Test
    void cannotPostExistentReview() throws UserNotFoundException, MovieNotInWatchlistException {
        // Given
        User user = easyRandom.nextObject(User.class);
        Movie movie = easyRandom.nextObject(Movie.class);
        PostReviewRequest request = new PostReviewRequest(
            user.getUserId(),
            movie.getId(),
            easyRandom.nextDouble(),
            easyRandom.nextObject(String.class)
        );

        LocalDateTime now = LocalDateTime.now();
        Review updatedReview = easyRandom.nextObject(Review.class);
        updatedReview.setReviewDate(now);
        updatedReview.setRating(request.rating());
        updatedReview.setComment(request.comment());

        UserMovie userMovie = new UserMovie(user, movie, easyRandom.nextBoolean());
        userMovie.setReview(updatedReview);
        updatedReview.setUserMovie(userMovie);
        user.addMovieToWatchlist(userMovie);

        Review storedReview = easyRandom.nextObject(Review.class);
        ReviewDto expectedResult = createRandomRecord(ReviewDto.class, easyRandom);

        Mockito.when(userRepository.findById(request.userId())).thenReturn(Optional.of(user));
        Mockito.when(reviewRepository.save(updatedReview)).thenReturn(storedReview); // any() because mocking LocalDate sucks
        Mockito.when(mapper.mapToDTO(storedReview)).thenReturn(expectedResult);

        // When
        ReviewDto result = reviewService.postReview(request);

        // Then
        assertEquals(expectedResult, result);
    }

    @Test
    void canPostNonExistentReview() throws UserNotFoundException, MovieNotInWatchlistException {
        // Given
        User user = easyRandom.nextObject(User.class);
        Movie movie = easyRandom.nextObject(Movie.class);
        PostReviewRequest request = new PostReviewRequest(
            user.getUserId(),
            movie.getId(),
            easyRandom.nextDouble(),
            easyRandom.nextObject(String.class)
        );
        UserMovie userMovie = new UserMovie(user, movie, easyRandom.nextBoolean());
        user.addMovieToWatchlist(userMovie);
        Review storedReview = easyRandom.nextObject(Review.class);
        ReviewDto expectedResult = createRandomRecord(ReviewDto.class, easyRandom);

        Mockito.when(userRepository.findById(request.userId())).thenReturn(Optional.of(user));
        Mockito.when(reviewRepository.save(Mockito.any())).thenReturn(storedReview); // any() because mocking LocalDate sucks
        Mockito.when(mapper.mapToDTO(storedReview)).thenReturn(expectedResult);

        // When
        ReviewDto result = reviewService.postReview(request);

        // Then
        assertEquals(expectedResult, result);
    }

    @Test
    void cannotPostReviewWhenUserNotFound() {
        // Given
        PostReviewRequest request = createRandomRecord(PostReviewRequest.class, easyRandom);

        Mockito.when(userRepository.findById(request.userId())).thenReturn(Optional.empty());

        // When -> Then
        assertThrows(UserNotFoundException.class, () -> reviewService.postReview(request));
    }

    @Test
    void cannotPostReviewWhenMovieNotFound() {
        // Given
        PostReviewRequest request = createRandomRecord(PostReviewRequest.class, easyRandom);
        User foundUser = new User(
                easyRandom.nextObject(String.class),
                easyRandom.nextObject(String.class),
                easyRandom.nextObject(String.class),
                easyRandom.nextObject(Role.class),
                easyRandom.nextBoolean(),
                easyRandom.nextBoolean()
        );

        Mockito.when(userRepository.findById(request.userId())).thenReturn(Optional.of(foundUser));

        // When -> Then
        assertThrows(MovieNotInWatchlistException.class, () -> reviewService.postReview(request));
    }

    @Test
    void canDeleteReviewSuccessfully() {
        // Given
        long reviewId = easyRandom.nextLong();
        Review review = easyRandom.nextObject(Review.class);
        UserMovie userMovie = Mockito.mock(UserMovie.class);
        review.setUserMovie(userMovie);

        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // When
        reviewService.deleteReview(reviewId);

        // Then
        Mockito.verify(reviewRepository).findById(reviewId);
        Mockito.verify(userMovie).setReview(null); // Verify interaction
        Mockito.verify(userMovieRepository).save(userMovie);
        Mockito.verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    void cannotDeleteReviewWhenNotFound() {
        // Given
        long reviewId = easyRandom.nextLong();

        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // When -> Then
        assertThrows(RuntimeException.class, () -> reviewService.deleteReview(reviewId));
        Mockito.verify(reviewRepository).findById(reviewId);
        Mockito.verifyNoInteractions(userMovieRepository);
    }
}
