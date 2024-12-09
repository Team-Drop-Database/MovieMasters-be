package movie_master.api.request;

import movie_master.api.model.friendship.FriendshipStatus;

public record FriendshipRequest(
        String username,
        FriendshipStatus status
) {}