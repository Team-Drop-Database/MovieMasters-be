package movie_master.api.controller;

import movie_master.api.model.Movie;
import movie_master.api.service.MovieService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<Movie> getAllMovies(@RequestParam(required = false) String title) {
        if (title != null) {
            return movieService.findByTitleContaining(title);
        }
        return movieService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable Long id) {
        Optional<Movie> movie = movieService.findById(id);
        return movie.map(value ->
                new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Boolean deleteMovie(@PathVariable Long id) {
        movieService.deleteById(id);
        return true;
    }
}
