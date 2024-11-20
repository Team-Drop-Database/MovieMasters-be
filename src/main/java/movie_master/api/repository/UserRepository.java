package movie_master.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import movie_master.api.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findById(long id);
}