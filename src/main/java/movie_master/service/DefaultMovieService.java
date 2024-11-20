package movie_master.service;

import movie_master.model.Movie;
import movie_master.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DefaultMovieService implements MovieService {
    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public List<Movie> findByTitleContaining(String title) {
        return movieRepository.findMovieByTitleContaining(title);
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
