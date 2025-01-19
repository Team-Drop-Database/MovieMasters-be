package movie_master.api.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import movie_master.api.model.Movie;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByTitleContaining(String title, Pageable pageable);
    List<Movie> findAllByGenres_Name(String genreName);

    long countByTitleContaining(String title);

    Optional<Movie> findByTitle(String title);
}
