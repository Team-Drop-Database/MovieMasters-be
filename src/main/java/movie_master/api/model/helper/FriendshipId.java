package movie_master.api.model.helper;


import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FriendshipId implements Serializable {

    private int userId;
    private int friendId;

    public FriendshipId() { }

    public FriendshipId(int userId, int friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipId that = (FriendshipId) o;
        return userId == that.userId && friendId == that.friendId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, friendId);
    }
}