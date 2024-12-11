package movie_master.api.service;

import movie_master.api.dto.UserDto;
import movie_master.api.exception.EmailTakenException;
import movie_master.api.exception.UserMovieNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.exception.UsernameTakenException;
import movie_master.api.mapper.UserDtoMapper;
import movie_master.api.model.Movie;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.model.role.Roles;
import movie_master.api.repository.UserMovieRepository;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class DefaultUserServiceTest {

    //TODO mock movie repository
    @Mock private UserRepository userRepository;
    @Mock private UserMovieRepository userMovieRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserDtoMapper userDtoMapper;
    @InjectMocks private DefaultUserService defaultUserService;

    private final RegisterUserRequest registerRequest = new RegisterUserRequest("mock@gmail.com",
            "mock1234", "12345678");

    @Test
    void registerUserSuccessfully() throws UsernameTakenException, EmailTakenException {
        // Given
        String encodedPassword = "#deijjdejide!";

        User userToCreate = new User(
            registerRequest.email(),
            registerRequest.username(),
            encodedPassword,
            Roles.ROLE_USER.name(),
            true
        );
        User createdUser = new User(
            "ervin.@gmail.com",
            "dedede",
            encodedPassword,
            Roles.ROLE_USER.name(),
            true
        );
        UserDto userDto = new UserDto(
            userToCreate.getUserId(),
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
        User user = new User("example@test.mail", "User McNameface", "password1234", "QA", true);
        Movie movie1 = new Movie(1, "Pulp Fiction", "Fun adventures", Date.from(Instant.now()), "en-US", "there", 9);
        Movie movie2 = new Movie(2, "Lock Stock & Two Smoking Barrels", "Fun adventures", Date.from(Instant.now()), "en-EN", "there", 9);
        Movie movie3 = new Movie(3, "Se7en", "Fun adventures", Date.from(Instant.now()), "en-US", "there", 9);
        Set<UserMovie> expectedResult = Set.of(
            new UserMovie(user, movie1, false),
            new UserMovie(user, movie2, false),
            new UserMovie(user, movie3, false)
        );
        for (UserMovie userMovie : expectedResult) {
            user.addMovieToWatchlist(userMovie);
        }

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        Set<UserMovie> result = defaultUserService.getWatchList(userId);

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

    // TODO: Hier nog tests voor die andere dingen?

    @Test
    void succesUpdateWatchList() throws UserNotFoundException, UserMovieNotFoundException {
        // Given
        Long userId = 1337L;
        Long movieId = 1L;

        User user = new User("example@test.mail", "User McNameface", "password1234", "QA", true);
        user.setUserId(userId);
        Movie movie1 = new Movie(1, "Pulp Fiction", "Fun adventures", Date.from(Instant.now()), "en-US", "there", 9);
        UserMovie userMovie = new UserMovie(user, movie1, false);
        user.addMovieToWatchlist(userMovie);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        // When
        UserMovie resultUserMovie = defaultUserService.updateWatchItemStatus(userId, movieId, true);

        // Then
        assertEquals(userMovie, resultUserMovie);
        assertTrue(resultUserMovie.isWatched());
    }

    @Test
    void failUpdateWatchList()  throws UserNotFoundException, UserMovieNotFoundException {
        // Given
        Long userId = 1337L;
        Long movieId = 7L;

        User user = new User("example@test.mail", "User McNameface", "password1234", "QA", true);
        user.setUserId(userId);
        Movie movie1 = new Movie(1, "Pulp Fiction", "Fun adventures", Date.from(Instant.now()), "en-US", "there", 9);
        UserMovie userMovie = new UserMovie(user, movie1, false);
        user.addMovieToWatchlist(userMovie);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        assertThrows(UserMovieNotFoundException.class, () -> defaultUserService.updateWatchItemStatus(userId, movieId, true));

        // Then
        assertFalse(userMovie.isWatched());
    }
    
    
}
