package movie_master.api.dto;

import movie_master.api.model.friendship.FriendshipStatus;
import java.time.LocalDateTime;

/**
 * Data transfer object that is being returned to the client
 * @param id
 * @param username
 * @param friendUsername
 * @param friendProfilePicture
 * @param friendId
 * @param status
 * @param friendshipDate
 */
public record FriendshipDto(Long id, String username, Long userId, String friendUsername, Long friendId, String friendProfilePicture, FriendshipStatus status, LocalDateTime friendshipDate) {}
