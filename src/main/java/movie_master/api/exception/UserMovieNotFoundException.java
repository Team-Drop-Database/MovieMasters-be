package movie_master.api.exception;

public class UserMovieNotFoundException extends Exception {
    public UserMovieNotFoundException(Long itemId) {
        super("UserMovie with id '%d' does not exist".formatted(itemId));
    }
}