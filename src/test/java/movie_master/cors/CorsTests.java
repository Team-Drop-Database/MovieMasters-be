package movie_master.cors;

import com.fasterxml.jackson.databind.ObjectMapper;
import movie_master.api.request.RegisterUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CorsTests {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RegisterUserRequest registerUserRequest = new RegisterUserRequest("cors@gmail.com", "cors1234", "cors12233");;
    @Value("${client.host}")
    private String CLIENT_HOST;

    @Test
    public void cant_register_user_with_disallowed_origin() throws Exception {
        String json = objectMapper.writeValueAsString(registerUserRequest);

        mockMvc.perform(options("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Origin", "http://localhost:2003")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void can_register_user_with_allowed_origin() throws Exception {
        String json = objectMapper.writeValueAsString(registerUserRequest);

        mockMvc.perform(options("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Origin", CLIENT_HOST)
                )
                .andExpect(status().isOk());
    }
}
