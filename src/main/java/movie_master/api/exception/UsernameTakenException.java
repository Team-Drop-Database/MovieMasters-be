package movie_master.api.exception;

/**
 * Custom exception
 */
public class UsernameTakenException extends Exception {

    public UsernameTakenException(String username) {
        super("Username: %s has already been taken".formatted(username));
    }
}
