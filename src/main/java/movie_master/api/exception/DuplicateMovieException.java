package movie_master.api.exception;

public class DuplicateMovieException extends RuntimeException {
    public DuplicateMovieException(String movieTitle) {
        super("Movie with title: %s already exists.".formatted(movieTitle));
    }
}
