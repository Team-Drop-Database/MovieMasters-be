package movie_master.api.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.Date;
import java.util.Set;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {
    @Id
    private long id;
    private String title;
    @JsonAlias("overview")
    @Column(length = 2000)
    private String description;
    @JsonAlias("original_language")
    private String language;
    @JsonAlias("release_date")
    private Date releaseDate;
    private double rating;
    @JsonAlias("poster_path")
    private String posterPath;

    @OneToMany(mappedBy = "movie")
    private Set<UserMovie> userMovies;

    public Movie(long id, String title, String description, Date releaseDate, String language, String posterPath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.language = language;
        this.posterPath = posterPath;
    }

    public Movie() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}