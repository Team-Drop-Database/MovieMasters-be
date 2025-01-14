package movie_master.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
public class UserMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JsonBackReference
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "movie", nullable = false)
    private Movie movie;

    @Column(nullable = false)
    private boolean watched;

    @OneToOne(mappedBy = "userMovie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "review")
    private Review review;

    public UserMovie(User user, Movie movie, boolean watched) {
        this.user = user;
        this.movie = movie;
        this.watched = watched;
    }

    public UserMovie() {}

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}
