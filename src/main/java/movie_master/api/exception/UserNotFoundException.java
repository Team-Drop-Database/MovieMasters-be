package movie_master.api.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(Long userId) {
        super("User with id '%d' does not exist".formatted(userId));
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}