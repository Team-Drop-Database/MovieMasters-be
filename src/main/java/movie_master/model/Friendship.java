package movie_master.model;

import jakarta.persistence.*;
import movie_master.model.helper.FriendshipId;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendship")
public class Friendship {

    @EmbeddedId
    private FriendshipId friendshipId;

    private String status;

    @Column(name = "friendship_date", nullable = false)
    private LocalDateTime friendshipDate = LocalDateTime.now();

    // Getters and Setters
    public FriendshipId getFriendshipId() {
        return friendshipId;
    }

    public void setFriendshipId(FriendshipId friendshipId) {
        this.friendshipId = friendshipId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getFriendshipDate() {
        return friendshipDate;
    }

    public void setFriendshipDate(LocalDateTime friendshipDate) {
        this.friendshipDate = friendshipDate;
    }
}