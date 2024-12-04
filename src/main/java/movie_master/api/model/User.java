package movie_master.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * User table for the database
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "user", uniqueConstraints = {@UniqueConstraint(columnNames = {"email", "username"})})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String email;
    private String username;
    private String password;
    private String profilePicture;
    private LocalDate dateJoined = LocalDate.now();
    private String roles;
    private boolean enabled;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference      
    private Set<UserMovie> watchlist;

    public User() {}

    public User(String email, String username, String password, String roles, boolean enabled) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.enabled = enabled;
        watchlist = new HashSet<>();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateJoined() {
        return dateJoined;
    }

    public String getRoles() {
        return roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void addMovieToWatchlist(UserMovie movie) {
        watchlist.add(movie);
        movie.setUser(this);
    }

    public Set<UserMovie> getWatchList() {
        return watchlist;
    }
}