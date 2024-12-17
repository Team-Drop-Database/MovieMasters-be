package movie_master.api.repository;

import movie_master.api.model.Review;
import movie_master.api.model.UserMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMovieRepository extends JpaRepository<UserMovie, Long> {

    @Query("SELECT u.review FROM UserMovie u WHERE u.movie.id = :id")
    List<Review> findReviewsByMovieId(@Param("id") long id);
}
