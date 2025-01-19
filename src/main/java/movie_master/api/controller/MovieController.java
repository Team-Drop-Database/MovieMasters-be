package movie_master.api.controller;

import movie_master.api.exception.GenreNotFoundException;
import movie_master.api.exception.GenresNotLoadedException;
import movie_master.api.model.Genre;
import movie_master.api.model.Movie;
import movie_master.api.service.MovieService;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
    public ResponseEntity<List<Movie>> getMoviesByTitle(@RequestParam String title,
                                    @RequestParam(required = false) Integer page) {
        page = page == null ? 0 : page;
        List<Movie> movies = movieService.findByTitleContaining(title, page);

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
     * Retrieve a list of movies based on a
     *  given list of genres.
     * 
     * @param genres A list of genres, e.g. 'thriller' or 'western'
     * @return A list of movies that fall under these genres
     */
    @GetMapping("/genrefilter")
    public ResponseEntity<Object> getMoviesByGenre(@RequestParam List<String> genres) {
        try {
            List<Movie> movies = new ArrayList<>();
            for (int i = 0; i < genres.size(); i++) {
                movies.addAll(movieService.findByGenre(genres.get(i)));
            }
            return ResponseEntity.ok(movies);
        } catch (GenreNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/genres")
    public ResponseEntity<Object> getMovieGenres() {
        try {
            List<Genre> genres = movieService.findAllGenres();
            return ResponseEntity.ok(genres);
        } catch (GenresNotLoadedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
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
