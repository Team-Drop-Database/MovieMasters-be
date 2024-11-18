package movie_master.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String profilePicture;

    @Column()
    private LocalDateTime joinedAt;

    @ManyToMany
    @JoinTable(
        name = "watchlist", 
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private Set<Movie> watchlist;

    public User(String username, String email, String password, String profilePicture, LocalDateTime joinedAt){
        this.username = username;
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
        this.joinedAt = joinedAt;
    }

    public long getId(){
        return id;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return password;
    }

    public void setProfilePicture(String profilePicture){
        this.profilePicture = profilePicture;
    }

    public String getProfilePicture(){
        return profilePicture;
    }


    public void getJoinedAt(LocalDateTime joinedAt){
        this.joinedAt = joinedAt;
    }

    public LocalDateTime getJoinedAt(){
        return joinedAt;
    }

    public Set<Movie> getWatchList(){
        return watchlist;
    }
}
