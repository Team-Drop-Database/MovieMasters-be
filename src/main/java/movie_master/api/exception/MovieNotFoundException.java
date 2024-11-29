package movie_master.api.exception;

public class MovieNotFoundException extends Exception {
    public MovieNotFoundException(Long movieId) {
        super("Movie with id '%d' does not exist".formatted(movieId));
    }
}
