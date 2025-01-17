package movie_master.api.repository;

import org.springframework.stereotype.Repository;

import movie_master.api.model.Genre;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {}
