package movie_master.api.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import movie_master.api.model.UserMovie;

@Repository
public interface UserMovieRepository extends JpaRepository<UserMovie, Long>{}
