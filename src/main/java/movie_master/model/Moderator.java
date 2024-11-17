package movie_master.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "moderator")
public class Moderator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int moderatorId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime assignedDate = LocalDateTime.now();

    public int getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(int moderatorId) {
        this.moderatorId = moderatorId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
    }
}