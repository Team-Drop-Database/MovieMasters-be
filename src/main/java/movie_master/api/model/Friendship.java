package movie_master.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import movie_master.api.model.friendship.FriendshipStatus;

import java.time.LocalDateTime;

/**
 * Friendship table for the database
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "friendship")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    @JsonBackReference
    private User friend;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    @Column(name = "friendship_date", nullable = false)
    private LocalDateTime friendshipDate;

    public Friendship() {
        this.friendshipDate = LocalDateTime.now();
    }

    public Friendship(User user, User friend, FriendshipStatus status) {
        this.user = user;
        this.friend = friend;
        this.status = status;
        this.friendshipDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFriend() {
        return friend;
    }

    @JsonProperty("friendUsername")
    public String getFriendUsername() {
        return friend.getUsername();  // Return the username of the friend
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public LocalDateTime getFriendshipDate() {
        return friendshipDate;
    }

    public void setFriendshipDate(LocalDateTime friendshipDate) {
        this.friendshipDate = friendshipDate;
    }
}