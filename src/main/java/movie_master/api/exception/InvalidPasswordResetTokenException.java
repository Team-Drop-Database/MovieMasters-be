package movie_master.api.exception;

public class InvalidPasswordResetTokenException extends Exception {

    public InvalidPasswordResetTokenException() {
        super("The password reset token in the URL is invalid");
    }
}
