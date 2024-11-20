package movie_master.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;
import com.fasterxml.jackson.databind.ObjectMapper;
import movie_master.api.request.RegisterUserRequest;
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
    private ObjectMapper objectMapper;
    private RegisterUserRequest firstRegisterUserRequest;
    private RegisterUserRequest secondRegisterUserRequest;
    private RegisterUserRequest thirdRegisterUserRequest;
    private RegisterUserRequest fourthRegisterUserRequest;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        firstRegisterUserRequest = new RegisterUserRequest("mock@gmail.com", "mock12", "mocked123");
        secondRegisterUserRequest = new RegisterUserRequest("mock@gmail.com", "mock123", "mocked123");
        thirdRegisterUserRequest = new RegisterUserRequest("mock1@gmail.com", "mock12", "mocked123");
        fourthRegisterUserRequest = new RegisterUserRequest("", "", "");
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

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest()).andExpect(content().string(containsString("Email: %s has already been taken".formatted(secondRegisterUserRequest.email()))));
    }

    @Test
    public void cant_register_user_since_username_is_already_taken() throws Exception {
        String json = objectMapper.writeValueAsString(thirdRegisterUserRequest);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest()).andExpect(content().string(containsString("Username: %s has already been taken".formatted(thirdRegisterUserRequest.username()))));
    }

    @Test
    public void cant_register_user_since_body_is_invalid() throws Exception {
        String json = objectMapper.writeValueAsString(fourthRegisterUserRequest);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
