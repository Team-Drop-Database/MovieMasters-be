package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import movie_master.api.dto.ReviewDto;
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
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        List<ReviewDto> allReviews = service.findAll();
        return ResponseEntity.ok(allReviews);
    }

    @GetMapping("/{amount}")
    public ResponseEntity<List<ReviewDto>> getReviewsByAmount(@PathVariable int amount) {
        List<ReviewDto> foundReviews = service.findByAmount(amount);
        return ResponseEntity.ok(foundReviews);
    }

    @GetMapping("/movie")
    public ResponseEntity<List<ReviewDto>> getReviewsByMovie(@RequestParam long movieId) {
        List<ReviewDto> foundReviews = service.findByMovie(movieId);
        return ResponseEntity.ok(foundReviews);
    }

    @PostMapping
    public ResponseEntity<Object> placeReview(
        HttpServletRequest httpServletRequest,
        @RequestBody PostReviewRequest request
    ) {
        try {
            ReviewDto postedReview = service.postReview(request);
            return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI())).body(postedReview);
        } catch (UserNotFoundException | MovieNotInWatchlistException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}
