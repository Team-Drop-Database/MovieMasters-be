package movie_master.api.exception;

/**
 * Custom exception to handle cases when
 *  a movie genre does not exist.
 */
public class GenreNotFoundException extends Exception {
    public GenreNotFoundException(String genreName) {
        super("Genre %s does not exist.".formatted(genreName));
    }
}