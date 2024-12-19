package movie_master.api.dto;

import movie_master.api.model.friendship.FriendshipStatus;

import java.time.LocalDateTime;

public class FriendshipDto {
    private final Long id;
    private final String friendUsername;
    private final FriendshipStatus status;
    private final LocalDateTime friendshipDate;

    public FriendshipDto(Long id, String friendUsername, FriendshipStatus status, LocalDateTime friendshipDate) {
        this.id = id;
        this.friendUsername = friendUsername;
        this.status = status;
        this.friendshipDate = friendshipDate;
    }

    public Long getId() {
        return id;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public LocalDateTime getFriendshipDate() {
        return friendshipDate;
    }
}

