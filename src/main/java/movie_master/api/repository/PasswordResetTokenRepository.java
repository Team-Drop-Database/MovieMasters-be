package movie_master.api.repository;

import movie_master.api.model.PasswordResetToken;
import movie_master.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByValue(String value);
    Optional<PasswordResetToken> findByUser(User user);
}
