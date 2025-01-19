package movie_master;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import movie_master.api.dto.UserDto;
import movie_master.api.model.Genre;
import movie_master.api.model.Movie;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.model.role.Role;
import movie_master.api.repository.GenreRepository;
import movie_master.api.repository.MovieRepository;
import movie_master.api.repository.UserRepository;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.service.UserService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Value("${tmdb.api-key}")
    private String apiKey;

    @Value("${default-user.name}")
    private String username;

    @Value("${default-user.password}")
    private String password;

    @Autowired
    public DataLoader(MovieRepository movieRepository, GenreRepository genreRepository, 
                      UserService userService, UserRepository userRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public void run(ApplicationArguments args) {
        List<Genre> genres = genreRepository.findAll();
        if (genres.isEmpty()) {
            if (apiKey.isEmpty()) {
                throw new IllegalArgumentException("Add TMDB_API_KEY to your env variables");
            }
            AddGenres();
        }

        List<Movie> movies = movieRepository.findAll();
        if (movies.isEmpty()) {
            if (apiKey.isEmpty()) {
                throw new IllegalArgumentException("Add TMDB_API_KEY to your env variables");
            }
            AddMovies();
        }
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            if (username.isEmpty()) {
                throw new IllegalArgumentException("Add DEFAULT_USER_NAME to your env variables");
            }
            if (password.isEmpty()) {
                throw new IllegalArgumentException("Add DEFAULT_USER_PASSWORD to your env variables");
            }
            AddUser();
            AddUserMovie();
        }
    }

    public void AddMovies() {OkHttpClient client = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        for(int i = 1; i <= 5; i++) {
        // Collecting movies from the movie database api
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/movie/top_rated?language=en-US&page="+i)
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

                // Retrieve the array of genre id's
                JsonNode idsNode = node.get("genre_ids");

                // Extract the array as a list of longs
                List<Long> ids = new ArrayList<>();
                if (idsNode != null && idsNode.isArray()) {
                    for (JsonNode numberNode : idsNode) {
                        ids.add(numberNode.asLong());
                    }
                }
                
                // Iterate over the id's, assign genre objects to this movie
                ids.forEach(id -> {
                    Optional<Genre> genreOpt = genreRepository.findById(id);
                    if (genreOpt.isEmpty()) {
                        // Change this later to an exception
                        System.err.println("Error: could not find genre!");
                    }
                    Genre genre = genreOpt.get();
                    movie.addGenre(genre);
                });

                // Save the movie
                movieRepository.save(movie);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        }

    }

    /**
     * Adds genres to the database
     */
    public void AddGenres() {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        Request request = new Request.Builder()
        .url("https://api.themoviedb.org/3/genre/movie/list?language=en")
        .get()
        .addHeader("accept", "application/json")
        .addHeader("Authorization", "Bearer %s".formatted(apiKey))
        .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;

            // Iterating over the movies and adding them to the database
            JsonNode arrayNode = objectMapper.readTree(response.body().string()).get("genres");
            for (JsonNode node : arrayNode) {
                Genre genre = objectMapper.treeToValue(node, Genre.class);
                genreRepository.save(genre);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void AddUser() {
        RegisterUserRequest user = new RegisterUserRequest(
                "%s@mail.com".formatted(username),
                username,
                password);
        try {
            UserDto createdUser = userService.register(user.email(), user.username(), user.password());
            userService.updateUserRole(createdUser.id(), Role.ROLE_MOD.toString(), Role.ROLE_MOD);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void AddUserMovie() {
        // Get the user from the database and empty the watchlist
        User user = userRepository.findByUsername(username).get();
        user.setWatchlist(new HashSet<>());

        // Take some movies
        List<Movie> sampleMovies = movieRepository.findAll().stream()
                .limit(7).collect(Collectors.toList());

        // Assign them to the user, set some on 'watched' others on 'unwatched'
        sampleMovies.forEach((movie) -> {
            UserMovie userMovie = new UserMovie(user, movie, Math.random() > 0.5);
            userMovie.setMovie(movie);
            user.addMovieToWatchlist(userMovie);
        });

        userRepository.save(user);
    }
}