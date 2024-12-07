package movie_master.api.exception;

public class EmailNotFoundException extends Exception {

    public EmailNotFoundException(String email) {
        super("User with email '%s' does not exist".formatted(email));
    }
}
