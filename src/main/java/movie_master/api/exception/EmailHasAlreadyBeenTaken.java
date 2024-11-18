package movie_master.api.exception;

/**
 * Custom exception
 */
public class EmailHasAlreadyBeenTaken extends Exception {

    public EmailHasAlreadyBeenTaken(String email) {
        super("Email: %s has already been taken".formatted(email));
    }
}
