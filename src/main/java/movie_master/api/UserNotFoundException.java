package movie_master.api;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(Long userId) {
        super("User with id '%d' does not exist".formatted(userId));
    }
}