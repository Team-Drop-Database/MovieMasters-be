package movie_master.api.exception;

/**
 * Custom exception to handle cases when a friendship already exists
 */
public class FriendshipAlreadyExistsException extends Exception {

    public FriendshipAlreadyExistsException(Long userId, Long friendId) {
        super("Friendship between user with id: %d and user with id: %d already exists".formatted(userId, friendId));
    }
}