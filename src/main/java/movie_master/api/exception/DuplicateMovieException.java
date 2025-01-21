package movie_master.api.exception;

public class DuplicateMovieException extends RuntimeException {

    public long movieId;

    public DuplicateMovieException(long movieId, String movieTitle) {
        super("Movie with title: %s already exists.".formatted(movieTitle));
        this.movieId = movieId;
    }
}
