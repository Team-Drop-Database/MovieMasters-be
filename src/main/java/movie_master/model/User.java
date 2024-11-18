package movie_master.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long user_id;

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

    public User(String username, String email, String password, String profilePicture, LocalDateTime joinedAt){
        this.username = username;
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
        this.joinedAt = joinedAt;
    }

    public long getId(){
        return user_id;
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
}
