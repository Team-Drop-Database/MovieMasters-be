package movie_master;

import movie_master.model.User;
import movie_master.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateUser() {
        // Create and save a new user
        User user = new User();
        user.setUsername("johndoe2");
        user.setEmail("john.doe2@example.com");
        user.setPassword("hashedpassword");

        User savedUser = userRepository.save(user);

        // Assert that the user is saved
        assertNotNull(savedUser);
        assertEquals("johndoe", savedUser.getUsername());
        assertEquals("john.doe@example.com", savedUser.getEmail());
    }
}