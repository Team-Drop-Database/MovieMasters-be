package movie_master.api.exception;

/**
 * Custom exception
 */
public class UsernameHasAlreadyBeenTaken extends Exception {

    public UsernameHasAlreadyBeenTaken(String username) {
        super("Username: %s has already been taken".formatted(username));
    }
}
