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

import java.time.LocalDate;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {
    private MovieRepository movieRepository;
    private UserRepository userRepository;

    @Autowired
    public DataLoader(MovieRepository movieRepository, UserRepository userRepository) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    public void run(ApplicationArguments args) {

        // SAMPLE DATA, DONT REMOVE:

        // User user = new User("b.obba@gmail.com", "bob", "12345", "dad", true);
        // userRepository.save(user);

        // Movie moviee = new Movie("dsd", new Date(), "english", "some description", "mario", 1);
        // movieRepository.save(moviee);

        // UserMovie userMovie = new UserMovie(user, moviee, true, 4.0);
        // user.addMovieToWatchlist(userMovie);
        // userMovie.setUser(user);
        // userMovie.setMovie(moviee);

        // userRepository.save(user);
        // movieRepository.save(moviee);

        // TODO only do request if table is empty
        // OkHttpClient client = new OkHttpClient();
        // ObjectMapper objectMapper = new ObjectMapper();

        // Request request = new Request.Builder()
        //         .url("https://api.themoviedb.org/3/movie/top_rated?language=en-US&page=1")
        //         .get()
        //         .addHeader("accept", "application/json")
        //         .addHeader("Authorization", "Bearer ")
        //         .build();

        // try (Response response = client.newCall(request).execute()) {
        //     assert response.body() != null;

        //     JsonNode arrayNode = objectMapper.readTree(response.body().string()).get("results");
        //     for (JsonNode node : arrayNode) {
        //         Movie movie = objectMapper.treeToValue(node, Movie.class);
        //         movieRepository.save(movie);
        //     }
        // } catch (Exception e){
        //     throw new RuntimeException(e);
        // }
    }
}
