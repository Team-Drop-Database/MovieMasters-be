package movie_master.api.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * User table for the database
 */
@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"email", "username"})})
public class User {
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Long id;
    private String email;
    private String username;
    private String password;
    private String profile_picture;
    private LocalDate date_joined = LocalDate.now();
    private String roles;
    private boolean enabled;

    public User() {

    }

    public User(String email, String username, String password, String roles, boolean enabled) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
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
        return profile_picture;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateJoined() {
        return date_joined;
    }

    public String getRoles() {
        return roles;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
