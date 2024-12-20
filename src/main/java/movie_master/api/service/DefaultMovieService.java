package movie_master.api.service;

import movie_master.api.model.Movie;
import movie_master.api.repository.MovieRepository;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultMovieService implements MovieService {

    private final int NUMBER_OF_MOVIES_PER_PAGE = 20;
    private final MovieRepository movieRepository;

    public DefaultMovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public List<Movie> findByTitleContaining(String title) {
        return movieRepository.findMovieByTitleContaining(title, Limit.of(NUMBER_OF_MOVIES_PER_PAGE));
    }

    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }

    public void deleteById(Long id) {
        movieRepository.deleteById(id);
    }

    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }
}
