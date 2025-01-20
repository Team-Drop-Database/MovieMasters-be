package movie_master.api.repository;

import jakarta.transaction.Transactional;
import movie_master.api.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Review r WHERE r.userMovie.user.userId = :userId")
    void deleteByUser(long userId);
}
