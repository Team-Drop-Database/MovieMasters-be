package movie_master.api.service;

import movie_master.api.model.Movie;
import movie_master.api.repository.MovieRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DefaultMovieService implements MovieService {

    private final int RESULTS_PER_PAGE = 10;
    private final MovieRepository movieRepository;

    public DefaultMovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> findByTitleContaining(String title, int page) {
        return movieRepository.findByTitleContaining(title, PageRequest.of(page, RESULTS_PER_PAGE));
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

    public int getNumberOfPages(String title) {
        return (int) Math.ceil((double) this.movieRepository.countByTitleContaining(title) / RESULTS_PER_PAGE);
    }
}
