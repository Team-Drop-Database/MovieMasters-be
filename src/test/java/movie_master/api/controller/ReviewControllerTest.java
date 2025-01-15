package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import movie_master.api.dto.ReviewDto;
import movie_master.api.exception.MovieNotFoundException;
import movie_master.api.exception.MovieNotInWatchlistException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.request.PostReviewRequest;
import movie_master.api.service.ReviewService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

import static movie_master.utils.TestUtils.createMultipleRandomRecords;
import static movie_master.utils.TestUtils.createRandomRecord;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock private ReviewService service;
    @InjectMocks private ReviewController controller;

    EasyRandom easyRandom = new EasyRandom();

    @Test
    void getAllReviewsTest() {
        // Given
        int elementAmount = 10;
        List<ReviewDto> expectedResult = createMultipleRandomRecords(ReviewDto.class, easyRandom, elementAmount);

        Mockito.when(service.findAll()).thenReturn(expectedResult);

        // When
        ResponseEntity<List<ReviewDto>> result = controller.getAllReviews();

        // Then
        assertEquals(result.getBody(), expectedResult);
    }

    @Test
    void getReviewsByAmountTest() {
        // Given
        int desiredAmount = 10;
        List<ReviewDto> expectedResult = createMultipleRandomRecords(ReviewDto.class, easyRandom, desiredAmount);

        Mockito.when(service.findByAmount(desiredAmount)).thenReturn(expectedResult);

        // When
        ResponseEntity<List<ReviewDto>> result = controller.getReviewsByAmount(desiredAmount);

        // Then
        assertEquals(result.getBody(), expectedResult);
    }

    @Test
    void placeReviewTest() throws UserNotFoundException, MovieNotFoundException {
        // Given
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        String uri = easyRandom.nextObject(String.class);
        PostReviewRequest request = createRandomRecord(PostReviewRequest.class, easyRandom);
        ReviewDto expectedResult = createRandomRecord(ReviewDto.class, easyRandom);

        Mockito.when(service.postReview(request)).thenReturn(expectedResult);
        Mockito.when(httpServletRequest.getRequestURI()).thenReturn(uri);
        // When
        ResponseEntity<Object> result = controller.placeReview(httpServletRequest, request);

        // Then
        assertEquals(result.getBody(), expectedResult);
    }

    @Test
    void placeReviewUserNotFoundTest() throws UserNotFoundException, MovieNotFoundException {
        // Given
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        PostReviewRequest request = createRandomRecord(PostReviewRequest.class, easyRandom);
        UserNotFoundException exception = new UserNotFoundException(request.userId());

        Mockito.when(service.postReview(request)).thenThrow(exception);

        // When
        ResponseEntity<Object> result = controller.placeReview(httpServletRequest, request);

        // Then
        assertEquals(result.getBody(), exception.getMessage());
    }

    @Test
    void placeReviewMovieNotFoundTest() throws UserNotFoundException, MovieNotInWatchlistException {
        // Given
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        PostReviewRequest request = createRandomRecord(PostReviewRequest.class, easyRandom);
        MovieNotInWatchlistException exception = new MovieNotInWatchlistException(request.movieId(), request.userId());

        Mockito.when(service.postReview(request)).thenThrow(exception);

        // When
        ResponseEntity<Object> result = controller.placeReview(httpServletRequest, request);

        // Then
        assertEquals(result.getBody(), exception.getMessage());
    }

    @Test
    void deleteReviewSuccessfulTest() {
        // Given
        long reviewId = easyRandom.nextObject(Long.class);

        // No exception is expected when the service deletes the review
        Mockito.doNothing().when(service).deleteReview(reviewId);

        // When
        ResponseEntity<Object> result = controller.deleteReview(reviewId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        Mockito.verify(service).deleteReview(reviewId);
    }

    @Test
    void deleteReviewFailureTest() {
        // Given
        long reviewId = easyRandom.nextObject(Long.class);
        RuntimeException exception = new RuntimeException("Error deleting review");

        // Mock service throwing an exception
        Mockito.doThrow(exception).when(service).deleteReview(reviewId);

        // When
        ResponseEntity<Object> result = controller.deleteReview(reviewId);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Mockito.verify(service).deleteReview(reviewId);
    }
}
