package movie_master.api.request;

import movie_master.api.model.friendship.FriendshipStatus;

public class FriendshipRequest {
    private Long userId;
    private Long friendId;
    private FriendshipStatus status;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }
}