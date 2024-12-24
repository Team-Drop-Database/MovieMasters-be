package movie_master.api.service;

import movie_master.api.dto.UserDto;
import movie_master.api.dto.UserMovie.UserMovieDto;
import movie_master.api.exception.EmailTakenException;
import movie_master.api.exception.UserMovieNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.exception.UsernameTakenException;
import movie_master.api.mapper.UserDtoMapper;
import movie_master.api.mapper.UserMovieDtoMapper;
import movie_master.api.model.Movie;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.repository.UserMovieRepository;
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

    // TODO: Hier nog tests voor die andere dingen?

    @Test
    void succesUpdateWatchList() throws UserNotFoundException, UserMovieNotFoundException {
        // Given
        Long userId = 1337L;
        Long movieId = 1L;

        User user = new User("example@test.mail", "User McNameface", "password1234", Role.ROLE_USER, true);
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

        User user = new User("example@test.mail", "User McNameface", "password1234", Role.ROLE_USER, true);
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

    @Test
    void succesRemoveFromWatchlist() throws UserNotFoundException, UserMovieNotFoundException{

        // Given
        Long userId = 1337L;
        Long removedMovieId = 1L;

        final int CORRECT_MOVIES_AMOUNT = 2;

        User user = new User("example@test.mail", "User McNameface", "password1234", Role.ROLE_USER, true);
        user.setUserId(userId);

        Movie movie1 = new Movie(1, "Pulp Fiction", "Fun adventures", Date.from(Instant.now()), "en-US", "there", 9);
        Movie movie2 = new Movie(2, "Lock Stock & Two Smoking Barrels", "Fun adventures", Date.from(Instant.now()), "en-EN", "there", 9);
        Movie movie3 = new Movie(3, "Se7en", "Fun adventures", Date.from(Instant.now()), "en-US", "there", 9);

        UserMovie deletedUserMovie = new UserMovie(user, movie1, false);
        user.addMovieToWatchlist(deletedUserMovie);
        user.addMovieToWatchlist(new UserMovie(user, movie2, false));
        user.addMovieToWatchlist(new UserMovie(user, movie3, false));

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userMovieRepository).delete(deletedUserMovie);

        // When
        defaultUserService.removeMovieFromWatchlist(userId, removedMovieId);

        // Verify the (now shrinked) size of the watchlist
        assertEquals(CORRECT_MOVIES_AMOUNT, user.getWatchList().size());
        assertFalse(user.getWatchList().contains(deletedUserMovie));
    }

    @Test
    void failRemoveFromWatchlist() throws UserNotFoundException, UserMovieNotFoundException {

        // Given
        Long userId = 1337L;
        Long removedMovieId = 5L;

        final int CORRECT_MOVIES_AMOUNT = 3;

        User user = new User("example@test.mail", "User McNameface", "password1234", Role.ROLE_USER, true);
        user.setUserId(userId);

        Movie movie1 = new Movie(1, "Pulp Fiction", "Fun adventures", Date.from(Instant.now()), "en-US", "there", 9);
        Movie movie2 = new Movie(2, "Lock Stock & Two Smoking Barrels", "Fun adventures", Date.from(Instant.now()), "en-EN", "there", 9);
        Movie movie3 = new Movie(3, "Se7en", "Fun adventures", Date.from(Instant.now()), "en-US", "there", 9);

        UserMovie attemptedDeletedUserMovie = new UserMovie(user, movie1, false);
        user.addMovieToWatchlist(attemptedDeletedUserMovie);
        user.addMovieToWatchlist(new UserMovie(user, movie2, false));
        user.addMovieToWatchlist(new UserMovie(user, movie3, false));

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When (also 'then'; since it checks if it throws the error)
        assertThrows(UserMovieNotFoundException.class, () -> defaultUserService.removeMovieFromWatchlist(userId, removedMovieId));

        // Then
        // Verify the (still unchanged) size of the watchlist
        assertEquals(CORRECT_MOVIES_AMOUNT, user.getWatchList().size());
        assertTrue(user.getWatchList().contains(attemptedDeletedUserMovie));
    }


    @Test
    void delete_user_succes() throws UserNotFoundException {
        Long userId = 1L;
        User existingUser = new User("existinguser@test.com", "existinguser", "password1234", Role.ROLE_USER, true);

        // Mock repository interactions, making sure the user exists
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        defaultUserService.deleteUserById(userId);

        // Verify if the correct methods are called.
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);

        // Checking if user still exists
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Verify that the user does not exist anymore
        assertFalse(userRepository.findById(userId).isPresent());
    }

    @Test
    void delete_user_not_found() {
        Long nonExistingId = 999L;

        // Mock the situation where the user does not exist
        Mockito.when(userRepository.existsById(nonExistingId)).thenReturn(false); // User does not exist

        // Verify the UserNotFoundException is thrown
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            defaultUserService.deleteUserById(nonExistingId);
        });

        // Optional: Controleer het bericht of de details van de gegooide uitzondering
        assertEquals("User with id '999' does not exist", exception.getMessage());

        // Verify that deleteById is never called because the user does not exist
        Mockito.verify(userRepository, Mockito.never()).deleteById(nonExistingId);

        // Verify that existsById was called to check if the user exists
        Mockito.verify(userRepository, Mockito.times(1)).existsById(nonExistingId);
    }

}
