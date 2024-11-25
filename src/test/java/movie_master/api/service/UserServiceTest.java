package movie_master.api.service;

import movie_master.api.dto.UserDto;
import movie_master.api.exception.EmailTakenException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.exception.UsernameTakenException;
import movie_master.api.mapper.UserDtoMapper;
import movie_master.api.model.Movie;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.model.role.Roles;
import movie_master.api.repository.UserRepository;
import movie_master.api.request.RegisterUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserDtoMapper userDtoMapper;
    @InjectMocks private UserService service;

    private final RegisterUserRequest registerRequest = new RegisterUserRequest("mock@gmail.com",
            "mock1234", "12345678");

    @Test
    void registerUserSuccessfully() throws UsernameTakenException, EmailTakenException {
        // Given
        String encodedpw = "volendam";
        User userToCreate = new User(
            registerRequest.email(),
            registerRequest.username(),
            encodedpw,
            Roles.USER.name(),
            true
        );
        User createdUser = new User(
            "ervin.@gmail.com",
            "dedede",
            encodedpw,
            Roles.USER.name(),
            true
        );
        UserDto userDto = new UserDto(
            userToCreate.getId(),
            userToCreate.getEmail(),
            userToCreate.getUsername(),
            userToCreate.getProfilePicture(),
            userToCreate.getDateJoined(),
            userToCreate.getRoles()
        );

        // any() because mocking datetimes sucks
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(createdUser);
        Mockito.when(userDtoMapper.apply(createdUser)).thenReturn(userDto);
        Mockito.when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByUsername(registerRequest.username())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn(encodedpw);

        // When
        UserDto registeredUser = service.register(registerRequest);

        // Then
        assertEquals(userDto, registeredUser);
    }

    @Test
    void throwsWhenEmailTaken() {
        // Given
        User foundUser = new User();
        Mockito.when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.of(foundUser));
        Mockito.when(userRepository.findByUsername(registerRequest.username())).thenReturn(Optional.empty());

        // When -> then
        assertThrows(EmailTakenException.class, () -> service.register(registerRequest));
    }

    @Test
    void throwsWhenUsernameTaken() {
        // Given
        User foundUser = new User();
        Mockito.when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByUsername(registerRequest.username())).thenReturn(Optional.of(foundUser));

        // When -> then
        assertThrows(UsernameTakenException.class, () -> service.register(registerRequest));
    }

    @Test
    void retrieveWatchlistSuccessfully() throws UserNotFoundException {
        // Given
        long userId = 69;
        User user = new User("example@test.mail", "User McNameface", "password1234", "QA", true);
        Movie movie1 = new Movie(1, "Pulp Fiction", "Fun adventures", Date.from(Instant.now()), "en-US", "there");
        Movie movie2 = new Movie(2, "Lock Stock & Two Smoking Barrels", "Fun adventures", Date.from(Instant.now()), "en-EN", "there");
        Movie movie3 = new Movie(3, "Se7en", "Fun adventures", Date.from(Instant.now()), "en-US", "there");
        Set<UserMovie> expectedResult = Set.of(
            new UserMovie(user, movie1, false, 0.0),
            new UserMovie(user, movie2, false, 0.0),
            new UserMovie(user, movie3, false, 0.0)
        );
        for (UserMovie userMovie : expectedResult) {
            user.addMovieToWatchlist(userMovie);
        }

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        Set<UserMovie> result = service.getWatchList(userId);

        // Then
        assertEquals(expectedResult, result);
    }

    @Test
    void failRetrievingWatchlist() {
        // Given
        long userId = 69;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When -> then
        assertThrows(UserNotFoundException.class, () -> service.getWatchList(userId));
    }
}
