package movie_master.api.exception;

/**
 * Custom exception to handle unauthorized friendship actions.
 */
public class UnauthorizedFriendshipActionException extends Exception {

    public UnauthorizedFriendshipActionException(Long userId, Long friendId) {
        super("User with id %d is not authorized to update the friendship status with user %d.".formatted(userId, friendId));
    }
}