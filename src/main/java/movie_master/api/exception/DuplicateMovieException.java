package movie_master.api.exception;

public class DuplicateMovieException extends Exception {
    public DuplicateMovieException(String movieTitle) {
        super("Movie with title: %s already exists.".formatted(movieTitle));
    }
}
