package movie_master.model;

import jakarta.persistence.*;

@Entity
@Table(name = "watchlist")
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int watchlistId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public int getWatchlistId() {
        return watchlistId;
    }

    public void setWatchlistId(int watchlistId) {
        this.watchlistId = watchlistId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}