package movie_master.api.repository;

import movie_master.api.model.WatchlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchlistEntryRepository extends JpaRepository<WatchlistEntry, Integer> {

    List<WatchlistEntry> findByWatchlist_WatchlistId(Integer watchlistId);

    List<WatchlistEntry> findByStatus(String status);
}