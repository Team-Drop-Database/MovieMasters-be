package movie_master.api.exception;

public class UserAlreadyHasPasswordResetToken extends Exception {

    public UserAlreadyHasPasswordResetToken() {
        super("Instructions for resetting your password have already been sent to you");
    }
}
