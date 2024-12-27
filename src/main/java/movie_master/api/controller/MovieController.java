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

    /**
     * Gets all movies or movies where the title contains the title parameter
     *
     * @param title title that should be in the movie title
     * @param page page number for pagination (starts at 0)
     * @return List of movies
     */
    @GetMapping
    public List<Movie> getAllMovies(@RequestParam(required = false) String title,
                                    @RequestParam(required = false) int page) {
        if (title != null) {
            return movieService.findByTitleContaining(title, page);
        }
        return movieService.findAll();
    }

    /**
     * Gets a movie by the given ID
     *
     * @param id ID of the movie
     * @return a movie object
     */
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable Long id) {
        Optional<Movie> movie = movieService.findById(id);
        return movie.map(value ->
                new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/pages")
    public ResponseEntity<Integer> getNumberOfPages(@RequestParam String title){
        return ResponseEntity.ok(this.movieService.getNumberOfPages(title));
    }

    /**
     * Deletes a movie by the given ID
     *
     * @param id ID of the movie to delete
     * @return boolean to indicate of the movie is deleted successful
     */
    @DeleteMapping("/{id}")
    public Boolean deleteMovie(@PathVariable Long id) {
        movieService.deleteById(id);
        return true;
    }
}
