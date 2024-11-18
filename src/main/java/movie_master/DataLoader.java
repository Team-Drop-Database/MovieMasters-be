package movie_master;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import movie_master.model.Movie;
import movie_master.model.User;
import movie_master.model.UserMovie;
import movie_master.repository.MovieRepository;
import movie_master.repository.UserRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cglib.core.Local;
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
        User user = new User("Bob", "b.ertje@gmail.com", "12345", "blablabla", LocalDateTime.now());
        userRepository.save(user);
        
        Movie starwarsMovie = new Movie("https://s2.qwant.com/thumbr/474x266/d/b/a8e1eb36ade7f72b895f4bec16cf3244776395b10e6c1a455afdc19bdbb41e/th.jpg?u=https%3A%2F%2Ftse.mm.bing.net%2Fth%3Fid%3DOIP.mq7s159Oqsy0Vv0TPQRdLgHaEK%26pid%3DApi&q=0&b=1&p=0&a=0", Date.from(Instant.now()), "english", "blablabla", "Star Trek", 23);
        movieRepository.save(starwarsMovie);
        UserMovie starwarsReview = new UserMovie(user, starwarsMovie, true, 4);
        user.addMovieToWatchlist(starwarsReview);
        starwarsReview.setMovie(starwarsMovie);

        Movie movie2 = new Movie("https://s2.qwant.com/thumbr/474x729/5/2/b8d9efeea00a7f0d36d92e8700f0b02813bbc9d0566c03ce4fcfaad0b04c0a/th.jpg?u=https%3A%2F%2Ftse.mm.bing.net%2Fth%3Fid%3DOIP.fxDUMakGNNPNmwP_sysnwwHaLZ%26pid%3DApi&q=0&b=1&p=0&a=0", Date.from(Instant.now()), "english", "blablabla", "The Super Mario Bros Movie", 21);
        movieRepository.save(movie2);
        UserMovie movie2Review = new UserMovie(user, movie2, true, 4);
        user.addMovieToWatchlist(movie2Review);
        movie2Review.setMovie(movie2);

        Movie movie3 = new Movie("https://m.media-amazon.com/images/S/pv-target-images/a8275e14cf7e2380ad1c6536d214e372c73c53908b26b7e95a70f68e3470d070.jpg", Date.from(Instant.now()), "english", "blablabla", "The Good, The Bad and The Ugly", 20);
        movieRepository.save(movie3);
        UserMovie movie3Review = new UserMovie(user, movie3, false, 4);
        user.addMovieToWatchlist(movie3Review);
        movie3Review.setMovie(movie3);

        userRepository.save(user);
        

        // user.addMovieToWatchlist(movie);
        // userRepository.save(user);

        //TODO only do request if table is empty
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
