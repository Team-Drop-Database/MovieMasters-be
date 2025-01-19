package movie_master.api.service;

import movie_master.api.exception.GenreNotFoundException;
import movie_master.api.exception.GenresNotLoadedException;
import movie_master.api.model.Genre;
import movie_master.api.exception.DuplicateMovieException;
import movie_master.api.model.Movie;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MovieService {
    List<Movie> findByTitleContaining(String title, int pageNumber);
    List<Movie> findByGenre(String genreName) throws GenreNotFoundException;
    List<Genre> findAllGenres() throws GenresNotLoadedException;

    Optional<Movie> findById(Long id);

    void deleteById(Long id);

    Movie save(Movie movie) throws DuplicateMovieException;

    int getNumberOfPages(String title);
}
