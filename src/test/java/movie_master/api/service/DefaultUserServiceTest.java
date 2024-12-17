package movie_master.api.service;

import movie_master.api.dto.UserDto;
import movie_master.api.dto.UserMovie.UserMovieDto;
import movie_master.api.exception.EmailTakenException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.exception.UsernameTakenException;
import movie_master.api.mapper.UserDtoMapper;
import movie_master.api.mapper.UserMovieDtoMapper;
import movie_master.api.model.Movie;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.model.role.Role;
import movie_master.api.repository.UserRepository;
import movie_master.api.request.RegisterUserRequest;
import movie_master.utils.TestUtils;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class DefaultUserServiceTest {

    //TODO mock movie repository
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserDtoMapper userDtoMapper;
    @Mock private UserMovieDtoMapper userMovieDtoMapper;
    @InjectMocks private DefaultUserService defaultUserService;

    private final RegisterUserRequest registerRequest = new RegisterUserRequest("mock@gmail.com",
            "mock1234", "12345678");

    EasyRandom easyRandom = new EasyRandom();

    @Test
    void registerUserSuccessfully() throws UsernameTakenException, EmailTakenException {
        // Given
        String encodedPassword = "#deijjdejide!";

        User userToCreate = new User(
            registerRequest.email(),
            registerRequest.username(),
            encodedPassword,
            Role.ROLE_USER,
            true
        );
        User createdUser = new User(
            "ervin.@gmail.com",
            "dedede",
            encodedPassword,
            Role.ROLE_USER,
            true
        );
        UserDto userDto = new UserDto(
            userToCreate.getUserId(),
            userToCreate.getEmail(),
            userToCreate.getUsername(),
            userToCreate.getProfilePicture(),
            userToCreate.getDateJoined(),
            userToCreate.getRole()
        );

        // any() because mocking datetimes sucks
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(createdUser);
        Mockito.when(userDtoMapper.apply(createdUser)).thenReturn(userDto);
        Mockito.when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByUsername(registerRequest.username())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn(encodedPassword);

        // When
        UserDto registeredUser = defaultUserService.register(registerRequest);

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
        assertThrows(EmailTakenException.class, () -> defaultUserService.register(registerRequest));
    }

    @Test
    void throwsWhenUsernameTaken() {
        // Given
        User foundUser = new User();
        Mockito.when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByUsername(registerRequest.username())).thenReturn(Optional.of(foundUser));

        // When -> then
        assertThrows(UsernameTakenException.class, () -> defaultUserService.register(registerRequest));
    }

    @Test
    void retrieveWatchlistSuccessfully() throws UserNotFoundException {
        // Given
        Long userId = 69L;
        User user = new User("example@test.mail", "User McNameface", "password1234", Role.ROLE_USER, true);
        Movie movie1 = new Movie(1, "Pulp Fiction", "Fun adventures", Date.from(Instant.now()), "en-US", "there", 9);
        Movie movie2 = new Movie(2, "Lock Stock & Two Smoking Barrels", "Fun adventures", Date.from(Instant.now()), "en-EN", "there", 9);
        Movie movie3 = new Movie(3, "Se7en", "Fun adventures", Date.from(Instant.now()), "en-US", "there", 9);
        List<UserMovie> userMovies = List.of(
            new UserMovie(user, movie1, false),
            new UserMovie(user, movie2, false),
            new UserMovie(user, movie3, false)
        );
        for (UserMovie userMovie : userMovies) {
            user.addMovieToWatchlist(userMovie);
        }
        List<UserMovieDto> mapped = TestUtils.createMultipleRandomRecords(UserMovieDto.class, easyRandom, userMovies.size());
        Set<UserMovieDto> expectedResult = new HashSet<>(mapped);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        for (int i = 0; i < userMovies.size(); i++) {
            Mockito.when(userMovieDtoMapper.mapUserMovieToDto(userMovies.get(i))).thenReturn(mapped.get(i));
        }

        // When
        Set<UserMovieDto> result = defaultUserService.getWatchList(userId);

        // Then
        assertEquals(expectedResult, result);
    }

    @Test
    void failRetrievingWatchlist() {
        // Given
        Long userId = 69L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When -> then
        assertThrows(UserNotFoundException.class, () -> defaultUserService.getWatchList(userId));
    }
}
