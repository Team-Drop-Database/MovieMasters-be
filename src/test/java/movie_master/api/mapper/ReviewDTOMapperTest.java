package movie_master.api.mapper;

import movie_master.api.dto.ReviewDTO;
import movie_master.api.model.Review;
import movie_master.api.model.User;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReviewDTOMapperTest {

    @InjectMocks ReviewDTOMapper mapper;

    EasyRandom easyRandom = new EasyRandom();

    @Test
    void testMapping() {
        // Given
        Review review = easyRandom.nextObject(Review.class);
        User user = review.getUserMovie().getUser();
        ReviewDTO expectedResult = new ReviewDTO(
            review.getReviewId(),
            user.getUsername(),
            user.getProfilePicture(),
            review.getUserMovie().getMovie().getTitle(),
            review.getRating(),
            review.getComment()
        );

        // When
        ReviewDTO result = mapper.mapToDTO(review);

        // Then
        assertEquals(expectedResult, result);
    }
}
