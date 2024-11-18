package movie_master.service;

import movie_master.model.Movie;
import movie_master.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public List<Movie> findByTitleContaining(String title) {
        return movieRepository.findMovieByTitleContaining(title);
    }
}
