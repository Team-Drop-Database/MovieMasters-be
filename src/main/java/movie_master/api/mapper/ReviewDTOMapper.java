package movie_master.api.mapper;

import movie_master.api.dto.ReviewDTO;
import movie_master.api.model.Review;
import movie_master.api.model.User;
import org.springframework.stereotype.Service;

@Service
public class ReviewDTOMapper {

    public ReviewDTO mapToDTO(Review review) {
        User user = review.getUserMovie().getUser();

        return new ReviewDTO(
            review.getReviewId(),
            user.getUsername(),
            user.getProfilePicture(),
            review.getUserMovie().getMovie().getTitle(),
            review.getRating(),
            review.getComment()
        );
    }
}
