package movie_master.api.controller;

import movie_master.api.model.Movie;
import movie_master.api.service.MovieService;

import org.springframework.beans.factory.annotation.Autowired;
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
}
