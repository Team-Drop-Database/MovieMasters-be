package movie_master.api.mapper;

import movie_master.api.dto.ReviewDto;
import movie_master.api.model.Review;
import movie_master.api.model.User;
import org.springframework.stereotype.Service;

@Service
public class ReviewDtoMapper {

    public ReviewDto mapToDTO(Review review) {
        User user = review.getUserMovie().getUser();

        return new ReviewDto(
            review.getReviewId(),
            user.getUsername(),
            user.getProfilePicture(),
            review.getUserMovie().getMovie().getTitle(),
            review.getRating(),
            review.getComment()
        );
    }
}
