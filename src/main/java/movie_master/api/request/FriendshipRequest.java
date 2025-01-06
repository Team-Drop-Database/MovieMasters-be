package movie_master.api.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Represents the body of an HTTP request for managing friendships.
 *
 * @param username - the username (required for all operations).
 * @param status   - the friendship status (optional or required for specific operations).
 */
public record FriendshipRequest(
        @NotBlank(message = "Username is required.") String username,
        String status
) {}