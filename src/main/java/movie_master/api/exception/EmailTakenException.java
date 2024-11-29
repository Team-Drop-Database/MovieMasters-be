package movie_master.api.exception;

/**
 * Custom exception
 */
public class EmailTakenException extends Exception {

    public EmailTakenException(String email) {
        super("Email: %s has already been taken".formatted(email));
    }
}
