package movie_master.api.request;

import movie_master.api.model.friendship.FriendshipStatus;

public class FriendshipRequest {
    private String username;
    private FriendshipStatus status;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }
}