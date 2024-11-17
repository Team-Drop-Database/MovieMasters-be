package movie_master.controller;

import movie_master.model.Movie;
import movie_master.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @GetMapping
    public List<Movie> getAllMovies(@RequestParam(required = false) String title) {
        if (title != null) {
            return movieService.findByTitleContaining(title);
        }
        return movieService.findAll();
    }

    @PostMapping
    public ResponseEntity<Boolean> addMovies() {
        boolean result = movieService.AddMovies();;
        return ResponseEntity.ok(result);
    }
}
