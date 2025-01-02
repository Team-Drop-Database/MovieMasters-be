package movie_master.api.dto;

import movie_master.api.model.friendship.FriendshipStatus;
import java.time.LocalDateTime;

/**
 * Data transfer object that is being returned to the client
 * @param id
 * @param friendUsername
 * @param status
 * @param friendshipDate
 */
public record FriendshipDto(Long id, String friendUsername, FriendshipStatus status, LocalDateTime friendshipDate) {}
