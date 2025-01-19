package movie_master.api.service;

import movie_master.api.exception.DuplicateMovieException;
import movie_master.api.model.Movie;
import movie_master.api.repository.MovieRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MovieServiceTests {
    @Autowired
    private MovieService movieService;

    @MockBean
    private MovieRepository movieRepository;

    private final Movie movie = new Movie(1, "Pulp Fiction", "Fun adventures", Date.from(Instant.now()), "en-US", "there", 9);

    @BeforeEach
    public void setUp() {
        Mockito.when(movieRepository.findById(278L)).thenReturn(Optional.of(movie));
        Mockito.when(movieRepository.save(movie)).thenReturn(movie);
    }

    @Test
    public void findByIdReturnsMovie() {
        Optional<Movie> found = movieService.findById(278L);
        Assertions.assertTrue(found.isPresent());
    }

    @Test
    public void findByIdReturnsNoMovie() {
        Optional<Movie> found = movieService.findById(0L);
        Assertions.assertTrue(found.isEmpty());
    }

    @Test
    public void deleteByIdRemovesMovie() {
        movieService.deleteById(278L);
    }

    @Test
    void saveReturnsMovie() throws DuplicateMovieException {
        // When
        Movie savedMovie = movieService.save(movie);

        // Then
        Assertions.assertNotNull(savedMovie);
        Assertions.assertEquals(movie.getId(), savedMovie.getId());
    }

    @Test
    void saveDuplicateMovieThrowsDuplicateMovieException() throws DuplicateMovieException {
        // When
        Mockito.when(movieService.save(movie)).thenThrow(DuplicateMovieException.class);

        // Then
        Assertions.assertThrows(DuplicateMovieException.class, () -> movieService.save(movie));
    }
}
