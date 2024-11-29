package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import movie_master.api.dto.ReviewDTO;
import movie_master.api.exception.MovieNotInWatchlistException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.request.PostReviewRequest;
import movie_master.api.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<ReviewDTO> allReviews = service.findAll();
        return ResponseEntity.ok(allReviews);
    }

    @GetMapping("/{amount}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByAmount(@PathVariable int amount) {
        List<ReviewDTO> foundReviews = service.findByAmount(amount);
        return ResponseEntity.ok(foundReviews);
    }

    @PostMapping
    public ResponseEntity<Object> placeReview(
        HttpServletRequest httpServletRequest,
        @RequestBody PostReviewRequest request
    ) {
        try {
            ReviewDTO postedReview = service.postReview(request);
            return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI())).body(postedReview);
        } catch (UserNotFoundException | MovieNotInWatchlistException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}
