package movie_master.api.exception;

/**
 * Thrown when the genres are not (yet) loaded in the database (for whatever reason).
 */
public class GenresNotLoadedException extends Exception {
    public GenresNotLoadedException() {
        super("No genres found in the database.");
    }
}
