package movie_master.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import movie_master.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public User findById(long id);
}