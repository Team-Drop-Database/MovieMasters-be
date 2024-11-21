package movie_master.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import movie_master.api.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerTests {
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void getAllMoviesSuccess() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date releaseDate = dateFormat.parse("2000-01-01");
        Movie movie1 = new Movie(
                278,
                "The Shawshank Redemption",
                "Description of movie 1",
                releaseDate,
                "en",
                "/image.png");
        Movie movie2 = new Movie(
                1,
                "Star Wars",
                "Description of movie 2",
                releaseDate,
                "en",
                "/image.png");

        List<Movie> movies = Arrays.asList(movie1, movie2);
        mockMvc.perform(get("/movies")
                .content(objectMapper.writeValueAsString(movies))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getMoviesByTitleReturnsMovies() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date releaseDate = dateFormat.parse("2000-01-01");
        Movie movie1 = new Movie(
                278,
                "The Shawshank Redemption",
                "Description of movie 1",
                releaseDate,
                "en",
                "/image.png");
        Movie movie2 = new Movie(
                1,
                "Star Wars",
                "Description of movie 2",
                releaseDate,
                "en",
                "/image.png");

        List<Movie> movies = Arrays.asList(movie1, movie2);
        this.mockMvc.perform(get("/movies?title=a")
                        .content(objectMapper.writeValueAsString(movies))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
