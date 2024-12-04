package movie_master;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import movie_master.api.model.Movie;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.repository.MovieRepository;
import movie_master.api.repository.UserRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {
    private MovieRepository movieRepository;
    private UserRepository userRepository;

    @Value("${tmdb.api-key}")
    private String apiKey;

    @Autowired
    public DataLoader(MovieRepository movieRepository, 
                        UserRepository userRepository) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    public void run(ApplicationArguments args) {

        // ADDING MOVIES //

        // Only adding movies if there are non in the database
        List<Movie> movies = movieRepository.findAll();
        if (!movies.isEmpty()) {
            return;
        }

        OkHttpClient client = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        // Collecting movies from the movie database api
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/movie/top_rated?language=en-US&page=1")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer %s".formatted(apiKey))
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;

            // Iterating over the movies and adding them to the database
            JsonNode arrayNode = objectMapper.readTree(response.body().string()).get("results");
            for (JsonNode node : arrayNode) {
                Movie movie = objectMapper.treeToValue(node, Movie.class);
                movie.setPosterPath("https://image.tmdb.org/t/p/original%s".formatted(movie.getPosterPath()));
                movie.setTmdbRating(
                        BigDecimal.valueOf(movie.getTmdbRating()).setScale(1, RoundingMode.UP).doubleValue());
                movieRepository.save(movie);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        // ADDING A USER WITH USERMOVIES //

        // Check if there's a user
        List<User> users = userRepository.findAll();
        if (!users.isEmpty()) {
            return;
        }

        // If not, create a new one
        User user = new User("test.user@gmail.com", "Bobs", 
        "Burgers", "user", true);
        userRepository.save(user);

        // Take some movies
        List<Movie> sampleMovies = movieRepository.findAll().stream()
        .limit(7).collect(Collectors.toList());

        // Assign them to the user, set some on 'watched' others on 'unwatched'
        sampleMovies.forEach((movie) -> {
            UserMovie userMovie = new UserMovie(user, movie, Math.random() > 0.5);
            userMovie.setMovie(movie);
            user.addMovieToWatchlist(userMovie);
        });

        // Save
        userRepository.save(user);
    }
}
