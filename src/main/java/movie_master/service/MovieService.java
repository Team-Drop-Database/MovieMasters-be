package movie_master.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import movie_master.model.Movie;
import movie_master.repository.MovieRepository;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    private final OkHttpClient client = new OkHttpClient();

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public List<Movie> findByTitleContaining(String title) {
        return movieRepository.findMovieByTitleContaining(title);
    }

    public boolean AddMovies() {
        String jsonString = "";

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode arrayNode = objectMapper.readTree(jsonString).get("results");
            for (JsonNode node : arrayNode) {
                Movie movie = objectMapper.treeToValue(node, Movie.class);
                movieRepository.save(movie);
            }
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
