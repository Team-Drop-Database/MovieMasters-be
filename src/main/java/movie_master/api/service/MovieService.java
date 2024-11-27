package movie_master.api.service;

import movie_master.api.exception.MovieNotFoundException;
import movie_master.api.model.Movie;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MovieService {
    List<Movie> findAll();

    List<Movie> findByTitleContaining(String title);

    Optional<Movie> findById(Long id);

    void deleteById(Long id);

    Movie save(Movie movie);
}
