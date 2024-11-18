package movie_master.repository;

import movie_master.model.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModeratorRepository extends JpaRepository<Moderator, Integer> {
    Moderator findByUser_UserId(Integer userId);
}