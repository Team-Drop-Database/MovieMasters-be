package movie_master.api.repository;

import jakarta.transaction.Transactional;
import movie_master.api.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * Deletes all reports that reference the given user
     *
     * @param userId    The user to clear the heat of
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Report r WHERE r.reportedUser.userId = :userId")
    void deleteByUser(long userId);
}
