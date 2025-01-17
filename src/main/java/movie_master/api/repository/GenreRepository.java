package movie_master.api.repository;

import org.springframework.stereotype.Repository;

import movie_master.api.model.Genre;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    List<Genre> findByName(String name);
}
