package movie_master;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import movie_master.api.model.Movie;
import movie_master.api.repository.MovieRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {
    private MovieRepository movieRepository;

    @Value("${tmdb.api-key}")
    private String apiKey;

    @Autowired
    public DataLoader(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public void run(ApplicationArguments args) {
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
                movieRepository.save(movie);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
