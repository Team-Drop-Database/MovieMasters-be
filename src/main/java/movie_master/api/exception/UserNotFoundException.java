package movie_master.api.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
        super("No users found.");
    }

    public UserNotFoundException(Long userId) {
        super("User with id '%d' does not exist".formatted(userId));
    }

    public UserNotFoundException(String user) {
        super("User with name or email '%s' does not exist".formatted(user));
    }
}