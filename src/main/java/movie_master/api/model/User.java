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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String username;
    private String password;
    private String profilePicture;
    private LocalDate dateJoined = LocalDate.now();
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
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getProfilePicture() {
        return this.profilePicture;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateJoined() {
        return this.dateJoined;
    }

    public String getRoles() {
        return this.roles;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
