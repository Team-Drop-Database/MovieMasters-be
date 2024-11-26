package movie_master.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;
import com.fasterxml.jackson.databind.ObjectMapper;
import movie_master.api.model.User;
import movie_master.api.model.role.Roles;
import movie_master.api.repository.UserRepository;
import movie_master.api.request.RegisterUserRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for the register endpoint
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private ObjectMapper objectMapper;
    private RegisterUserRequest firstRegisterUserRequest;
    private RegisterUserRequest secondRegisterUserRequest;
    private RegisterUserRequest thirdRegisterUserRequest;
    private RegisterUserRequest fourthRegisterUserRequest;
    private Long testUserId;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        firstRegisterUserRequest = new RegisterUserRequest("mock@gmail.com", "mock12", "mocked123");
        secondRegisterUserRequest = new RegisterUserRequest("mock@gmail.com", "mock123", "mocked123");
        thirdRegisterUserRequest = new RegisterUserRequest("mock1@gmail.com", "mock12", "mocked123");
        fourthRegisterUserRequest = new RegisterUserRequest("", "", "");
        User user = new User("user@gmail.com", "user123", "password123",
                Roles.USER.name(), true);
        user = userRepository.save(user);
        testUserId = user.getId();
    }

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    public void register_user() throws Exception {
        String json = objectMapper.writeValueAsString(firstRegisterUserRequest);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void cant_register_user_since_email_is_already_taken() throws Exception {
        String json = objectMapper.writeValueAsString(secondRegisterUserRequest);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest()).andExpect(content().string(containsString("Email: %s has already been taken".formatted(secondRegisterUserRequest.email()))));
    }

    @Test
    public void cant_register_user_since_username_is_already_taken() throws Exception {
        String json = objectMapper.writeValueAsString(thirdRegisterUserRequest);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest()).andExpect(content().string(containsString("Username: %s has already been taken".formatted(thirdRegisterUserRequest.username()))));
    }

    @Test
    public void cant_register_user_since_body_is_invalid() throws Exception {
        String json = objectMapper.writeValueAsString(fourthRegisterUserRequest);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_user_success() throws Exception {
        assertTrue(userRepository.existsById(testUserId));

        mockMvc.perform(delete("/users/" + testUserId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        assertFalse(userRepository.existsById(testUserId));
    }

    @Test
    public void delete_user_not_found() throws Exception {
        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found: User with id '999' does not exist"));
    }
}
