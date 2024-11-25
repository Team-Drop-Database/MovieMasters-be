package movie_master.service;

import movie_master.api.dto.UserDto;
import movie_master.api.exception.EmailHasAlreadyBeenTaken;
import movie_master.api.exception.UsernameHasAlreadyBeenTaken;
import movie_master.api.mapper.UserDtoMapper;
import movie_master.api.model.User;
import movie_master.api.model.role.Roles;
import movie_master.api.repository.UserRepository;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.service.DefaultUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserDtoMapper userDtoMapper;
    @InjectMocks
    private DefaultUserService defaultUserService;

    private final RegisterUserRequest registerUserRequest = new RegisterUserRequest("mock@gmail.com",
            "mock1234", "12345678");

    @Test
    public void canRegisterUser() throws UsernameHasAlreadyBeenTaken, EmailHasAlreadyBeenTaken {
        String encodedpw = "volendam";
        Mockito.when(userRepository.findByEmail(registerUserRequest.email())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByUsername(registerUserRequest.username())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(registerUserRequest.password())).thenReturn(encodedpw);

        User userToCreate = new User(
            registerUserRequest.email(),
            registerUserRequest.username(),
            encodedpw,
            Roles.USER.name(),
            true);

        User createdUser = new User(
                "ervin.@gmail.com",
                "dedede",
                encodedpw,
                Roles.USER.name(),
                true);

        System.out.println(userToCreate.getPassword());

        UserDto userDto = new UserDto(userToCreate.getId(), userToCreate.getEmail(),
                userToCreate.getUsername(), userToCreate.getProfilePicture(),
                userToCreate.getDateJoined(), userToCreate.getRoles());

        Mockito.when(userRepository.save(userToCreate)).thenReturn(createdUser);

        Mockito.when(userDtoMapper.apply(createdUser)).thenReturn(userDto);

        UserDto registeredUser = defaultUserService.register(registerUserRequest);
        assertEquals(userDto, registeredUser);

        //
    }

}
