package movie_master.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import movie_master.api.exception.DuplicateMovieException;
import movie_master.api.model.Movie;
import movie_master.api.service.DefaultMovieService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @Value("${jwt.testing}")
    private String jwtTesting;

    @Mock
    private DefaultMovieService defaultMovieService;

    @InjectMocks
    private MovieController movieController;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void getMoviesByTitleReturnsMovies() throws Exception {
        List<Movie> movies = getMovies();
        this.mockMvc.perform(get("/movies?title=a")
                        .header("Authorization", "Bearer %s".formatted(jwtTesting))
                        .content(objectMapper.writeValueAsString(movies))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getMoviesByTitleWithBigPageNumberReturnsNotFound() throws Exception {
        List<Movie> movies = getMovies();
        this.mockMvc.perform(get("/movies?title=a&page=100000")
                        .header("Authorization", "Bearer %s".formatted(jwtTesting))
                        .content(objectMapper.writeValueAsString(movies))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMoviesByTitleWithNoMatchingTitleReturnsNotFound() throws Exception {
        this.mockMvc.perform(get("/movies?title=Batman: Under the Red Hood Not Found&page=0")
                        .header("Authorization", "Bearer %s".formatted(jwtTesting))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void userCanSaveMovie() throws Exception {
        // Given
        Movie movie = getMovies().getFirst();
        DuplicateMovieException exception = new DuplicateMovieException(movie.getTitle());
        Mockito.when(defaultMovieService.save(movie)).thenThrow(exception);

        // When
        ResponseEntity<Object> response = movieController.saveMovie(movie);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void normalUserCantSaveMovie() throws Exception {
        this.mockMvc.perform(post("/movies")
                .content("")
                .header("Authorization", "Bearer %s".formatted(jwtTesting)))
                .andExpect(status().isForbidden());
    }

    @NotNull
    private static List<Movie> getMovies() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date releaseDate = dateFormat.parse("2000-01-01");
            Movie movie1 = new Movie(
                    278,
                    "The Shawshank Redemption",
                    "Description of movie 1",
                    releaseDate,
                    "en",
                    "/image.png",
                    10);
            Movie movie2 = new Movie(
                    1,
                    "Star Wars",
                    "Description of movie 2",
                    releaseDate,
                    "en",
                    "/image.png",
                    10);

            return Arrays.asList(movie1, movie2);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
