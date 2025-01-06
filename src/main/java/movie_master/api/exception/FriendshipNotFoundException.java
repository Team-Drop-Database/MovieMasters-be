package movie_master.api.exception;

/**
 * Custom exception to handle cases when a friendship does not exist.
 */
public class FriendshipNotFoundException extends Exception {

    public FriendshipNotFoundException(Long userId, Long friendId) {
        super("Friendship between user with id: %d and user with id: %d does not exist".formatted(userId, friendId));
    }
}