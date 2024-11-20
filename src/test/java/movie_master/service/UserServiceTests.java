package movie_master.service;

import movie_master.api.dto.UserDto;
import movie_master.api.exception.EmailHasAlreadyBeenTaken;
import movie_master.api.exception.UsernameHasAlreadyBeenTaken;
import movie_master.api.mapper.UserDtoMapper;
import movie_master.api.model.User;
import movie_master.api.model.role.Roles;
import movie_master.api.request.RegisterUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the MockDefaultRegisterUserService class
 */
@SpringBootTest
public class UserServiceTests {
    private User user;
    private UserDtoMapper userDtoMapper;
    private PasswordEncoder passwordEncoder;
    private MockDefaultUserService mockDefaultRegisterUserService;
    private RegisterUserRequest firstRegisterUserRequest;
    private RegisterUserRequest secondRegisterUserRequest;
    private RegisterUserRequest thirdRegisterUserRequest;

    @BeforeEach
    void setup() {
        user = new User("mock@gmail.com", "mock", "123", Roles.USER.name(), true);
        userDtoMapper = new UserDtoMapper();
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        mockDefaultRegisterUserService = new MockDefaultUserService(new ArrayList<>(), passwordEncoder, userDtoMapper);
        firstRegisterUserRequest = new RegisterUserRequest("mock@gmail.com", "mock12", "mocked123");
        secondRegisterUserRequest = new RegisterUserRequest("mock@gmail.com", "mock123", "mocked123");
        thirdRegisterUserRequest = new RegisterUserRequest("mock1@gmail.com", "mock12", "mocked123");
    }

    @Test
    void user_object_can_be_transformed_to_user_dto_object() {
        UserDto userDto = userDtoMapper.apply(user);
        assertEquals(userDto.getClass(), UserDto.class);
    }

    @Test
    void user_password_gets_encrypted() {
        String encrypted_password = passwordEncoder.encode(user.getPassword());
        assertNotEquals(encrypted_password, user.getPassword());
    }

    @Test
    void can_register_user() throws UsernameHasAlreadyBeenTaken, EmailHasAlreadyBeenTaken {
        mockDefaultRegisterUserService.register(firstRegisterUserRequest);

        assertEquals(mockDefaultRegisterUserService.getRegisteredUsers().size(), 1);
    }

    @Test
    void cant_register_user_with_taken_email() throws UsernameHasAlreadyBeenTaken, EmailHasAlreadyBeenTaken {
        mockDefaultRegisterUserService.register(firstRegisterUserRequest);

        assertThrows(EmailHasAlreadyBeenTaken.class, () -> mockDefaultRegisterUserService.register(secondRegisterUserRequest));
    }

    @Test
    void cant_register_user_with_taken_username() throws UsernameHasAlreadyBeenTaken, EmailHasAlreadyBeenTaken {
        mockDefaultRegisterUserService.register(firstRegisterUserRequest);

        assertThrows(UsernameHasAlreadyBeenTaken.class, () -> mockDefaultRegisterUserService.register(thirdRegisterUserRequest));
    }
}
