package movie_master.api.dto;

import movie_master.api.model.friendship.FriendshipStatus;
import java.time.LocalDateTime;

/**
 * Data transfer object that is being returned to the client
 * @param id
 * @param username
 * @param friendUsername
 * @param friendProfilePicture
 * @param status
 * @param friendshipDate
 */
public record FriendshipDto(Long id, String username, String friendUsername, String friendProfilePicture, FriendshipStatus status, LocalDateTime friendshipDate) {}
