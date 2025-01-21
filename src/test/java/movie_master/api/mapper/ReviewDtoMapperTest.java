package movie_master.api.mapper;

import movie_master.api.dto.ReviewDto;
import movie_master.api.model.Review;
import movie_master.api.model.User;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReviewDtoMapperTest {

    @InjectMocks
    ReviewDtoMapper mapper;

    EasyRandom easyRandom = new EasyRandom();

    @Test
    void canMapReview() {
        // Given
        Review review = easyRandom.nextObject(Review.class);
        User user = review.getUserMovie().getUser();
        ReviewDto expectedResult = new ReviewDto(
            review.getReviewId(),
            user.getUserId(),
            user.getUsername(),
            user.getProfilePicture(),
            review.getUserMovie().getMovie().getTitle(),
            review.getRating(),
            review.getComment()
        );

        // When
        ReviewDto result = mapper.mapToDTO(review);

        // Then
        assertEquals(expectedResult, result);
    }
}
