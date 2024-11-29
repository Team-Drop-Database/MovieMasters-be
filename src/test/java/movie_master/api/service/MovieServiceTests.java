package movie_master.api.service;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MovieServiceTests {
    @Autowired
    private MovieService movieService;

    public MovieServiceTests() throws ParseException { }

    @MockBean
    private MovieRepository movieRepository;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date releaseDate = dateFormat.parse("2000-01-01");
    private final Movie movie1 = new Movie(
            278,
            "The Shawshank Redemption",
            "Description of movie 1",
            releaseDate,
            "en",
            "/image.png",
            10);

    @BeforeEach
    public void setUp() {
        Mockito.when(movieRepository.findById(278L)).thenReturn(Optional.of(movie1));
        Mockito.when(movieRepository.save(movie1)).thenReturn(movie1);
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
    public void saveReturnsMovie() throws ParseException {
        Movie savedMovie = movieService.save(movie1);

        Assertions.assertNotNull(savedMovie);
        Assertions.assertEquals(movie1.getId(), savedMovie.getId());
    }
}
