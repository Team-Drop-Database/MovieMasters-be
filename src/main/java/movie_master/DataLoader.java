package movie_master;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import movie_master.api.dto.UserDto;
import movie_master.api.exception.MovieNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.model.Movie;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.repository.MovieRepository;
import movie_master.api.repository.UserRepository;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.service.MovieService;
import movie_master.api.service.UserService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {
    private final MovieRepository movieRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final MovieService movieService;

    @Value("${tmdb.api-key}")
    private String apiKey;

    @Value("${default-user.name}")
    private String username;

    @Value("${default-user.password}")
    private String password;

    @Autowired
    public DataLoader(MovieRepository movieRepository,
                      UserService userService, UserRepository userRepository, MovieService movieService) {
        this.movieRepository = movieRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.movieService = movieService;
    }

    public void run(ApplicationArguments args) throws UserNotFoundException, MovieNotFoundException {
        AddMovies();
        AddUser();
        //AddUserMovie();
    }

    public void AddMovies(){
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
                movieRepository.save(movie);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void AddUser(){
        List<User> users = userRepository.findAll();
        if (!users.isEmpty()) {
            return;
        }

        RegisterUserRequest user = new RegisterUserRequest(
                "%s@mail.com".formatted(username),
                username,
                password);
        try {
            userService.register(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void AddUserMovie() {
        User user = userRepository.findByUsername(username).get();
        user.addMovieToWatchlist(new UserMovie(
                user,
                movieService.findAll().getFirst(),
                false));
        userRepository.save(user);
    }
}