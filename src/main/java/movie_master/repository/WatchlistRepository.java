package movie_master.repository;

import movie_master.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Integer> {
    Optional<Watchlist> findByUser_UserId(Integer userId);
}