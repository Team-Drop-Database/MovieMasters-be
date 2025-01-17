package movie_master.api.exception;

public class InvalidPasswordTokenException extends Exception {

    public InvalidPasswordTokenException() {
        super("The password reset token in the URL is invalid");
    }
}
