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
    public ResponseEntity<List<Movie>> getAllMovies(@RequestParam(required = false) String title,
                                    @RequestParam(required = false) Integer page) {
        List<Movie> movies;
        if (title != null) {
            page = page == null ? 0 : page;
            movies =  movieService.findByTitleContaining(title, page);
        } else {
            movies =  movieService.findAll();
        }

        if (movies.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(movies);
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

    /**
     * Returns the number of pages from a movie search
     *
     * @param title title of the movie we want to know the number of pages of
     * @return number representing the number of pages
     */
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
