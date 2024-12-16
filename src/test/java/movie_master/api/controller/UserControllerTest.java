package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import movie_master.api.dto.UserDto;
import movie_master.api.exception.*;
import movie_master.api.exception.MovieNotFoundException;
import movie_master.api.exception.UserMovieNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.exception.UsernameTakenException;
import movie_master.api.model.Movie;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.model.role.Role;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.service.DefaultUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private DefaultUserService defaultUserService;
    @InjectMocks private UserController userController;

    @Test
    void registerSuccessfully() throws EmailTakenException, UsernameTakenException {
        // Given
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        String uri = "uri";
        RegisterUserRequest request = new RegisterUserRequest("email", "username", "password");
        UserDto expectedBody = new UserDto(1L, "email", "username", "ugly", LocalDate.now(), Role.ROLE_USER);

        Mockito.when(defaultUserService.register(request)).thenReturn(expectedBody);
        Mockito.when(httpServletRequest.getRequestURI()).thenReturn(uri);

        // When
        ResponseEntity<Object> result = userController.register(httpServletRequest, request);

        // Then
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.CREATED.value()));
        assertEquals(result.getBody(), expectedBody);
    }

    @Test
    void registerEmailTaken() throws EmailTakenException, UsernameTakenException {
        // Given
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        RegisterUserRequest request = new RegisterUserRequest("email", "username", "password");
        EmailTakenException exception = new EmailTakenException("Email already exists");

        Mockito.when(defaultUserService.register(request)).thenThrow(exception);

        // When
        ResponseEntity<Object> result = userController.register(httpServletRequest, request);

        // Then
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
        assertEquals(result.getBody(), exception.getMessage());
    }

    @Test
    void registerUsernameTaken() throws EmailTakenException, UsernameTakenException {
        // Given
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        RegisterUserRequest request = new RegisterUserRequest("email", "username", "password");
        UsernameTakenException exception = new UsernameTakenException("Username already exists");

        Mockito.when(defaultUserService.register(request)).thenThrow(exception);

        // When
        ResponseEntity<Object> result = userController.register(httpServletRequest, request);

        // Then
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
        assertEquals(result.getBody(), exception.getMessage());
    }

    @Test
    void retrieveWatchlistSuccessfully() throws UserNotFoundException {
        // Given
        long userId = 1337;
        User user = new User("example@test.mail", "User McNameface", "password1234", Role.ROLE_USER, true);
        Movie movie1 = new Movie(1, "Pulp Fiction", "Fun adventures", Date.from(Instant.now()), "en-US", "there", 9);
        Movie movie2 = new Movie(2, "Lock Stock & Two Smoking Barrels", "Fun adventures", Date.from(Instant.now()), "en-EN", "there", 9);
        Movie movie3 = new Movie(3, "Se7en", "Fun adventures", Date.from(Instant.now()), "en-US", "there", 9);
        Set<UserMovie> expectedBody = Set.of(
            new UserMovie(user, movie1, false),
            new UserMovie(user, movie2, false),
            new UserMovie(user, movie3, false)
        );

        Mockito.when(defaultUserService.getWatchList(userId)).thenReturn(expectedBody);

        // When
        ResponseEntity<Object> result = userController.getWatchList(userId);

        // Then
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.OK.value()));
        assertEquals(result.getBody(), expectedBody);
    }

    @Test
    void failRetrievingWatchlist() throws UserNotFoundException {
        // Given
        long userId = 1337;
        UserNotFoundException exception = new UserNotFoundException(userId);
        String expectedMessage = String.format("User with id '%d' does not exist", userId);

        Mockito.when(defaultUserService.getWatchList(userId)).thenThrow(exception);

        // When
        ResponseEntity<Object> result = userController.getWatchList(userId);

        // Then
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()));
        assertEquals(result.getBody(), expectedMessage);
    }

    @Test
    void userCanBeFoundByUserName() throws UserNotFoundException {
        // Given
        String username = "User McNameface";
        UserDto user = new UserDto(1L, "test@mail.com", username, "ugly", LocalDate.now(), Role.ROLE_USER);

        Mockito.when(defaultUserService.getUserByUsername(username)).thenReturn(user);

        // When
        ResponseEntity<Object> result = userController.getUserByUsername(username);

        // Then
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }

    @Test
    void failUserCanBeFoundByUserName() throws UserNotFoundException {
        // Given
        String username = "User McNameface";

        Mockito.when(defaultUserService.getUserByUsername(username)).thenThrow(UserNotFoundException.class);

        // When
        ResponseEntity<Object> result = userController.getUserByUsername(username);

        // Then
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void userCanBeFoundByEmail() throws EmailNotFoundException {
        // Given
        String email = "test@user.com";
        UserDto user = new UserDto(1L, email, "User McNameface", "ugly", LocalDate.now(), Role.ROLE_USER);

        Mockito.when(defaultUserService.getUserByEmail(email)).thenReturn(user);

        // When
        ResponseEntity<Object> result = userController.getUserByEmail(email);

        // Then
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }

    @Test
    void failUserCanBeFoundByEmail() throws EmailNotFoundException {
        // Given
        String email = "test@user.com";
        UserDto user = new UserDto(1L, email, "User McNameface", "ugly", LocalDate.now(), Role.ROLE_USER);

        Mockito.when(defaultUserService.getUserByEmail(email)).thenThrow(EmailNotFoundException.class);

        // When
        ResponseEntity<Object> result = userController.getUserByEmail(email);

        // Then
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void successAddMovieToWatchlist() throws UserNotFoundException, MovieNotFoundException {
        // Given
        long userId = 1337;
        User user = new User("example@test.mail", "User McNameface", "password1234", Role.ROLE_USER, true);
        Movie movie1 = new Movie(1, "Pulp Fiction", "Fun adventures", Date.from(Instant.now()), "en-US", "there", 9);

        UserMovie expectedBody = new UserMovie(user, movie1, false);
        Map<String, Object> expectedMessage = Map.of(
                "message", "Successfully added to watchlist",
                "userId", userId,
                "movieId", movie1.getId(),
                "association_object", expectedBody
        );

        Mockito.when(defaultUserService.addMovieToWatchlist(userId, movie1.getId())).thenReturn(expectedBody);

        // When
        ResponseEntity<Object> result = userController.addMovieToWatchlist(userId, movie1.getId());

        // Then
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.OK.value()));
        assertEquals(result.getBody(), expectedMessage);
    }

    @Test
    void failAddMovieToWatchlist() throws UserNotFoundException, MovieNotFoundException {
        // Given
        Long userId = 1337L;
        Long movieId = 1L;
        Long wrongMovieId = 2L;

        MovieNotFoundException exception = new MovieNotFoundException(movieId);
        String expectedMessage = "Could not associate user with movie. Exception message: " + exception.getMessage();

        Mockito.when(defaultUserService.addMovieToWatchlist(userId, wrongMovieId)).thenThrow(exception);

        // When
        ResponseEntity<Object> result = userController.addMovieToWatchlist(userId, wrongMovieId);

        // Then
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.NOT_ACCEPTABLE.value()));
        assertEquals(result.getBody(), expectedMessage);
    }

    @Test
    void succesUpdateWatchList() throws UserNotFoundException, UserMovieNotFoundException {

        // Given
        Long userId = 1337L;
        Long watchlistItemId = 1L;

        User user = new User("example@test.mail", "User McNameface", "password1234", Role.ROLE_USER, true);
        Movie movie1 = new Movie(1, "Pulp Fiction", "Fun adventures", Date.from(Instant.now()), "en-US", "there", 9);

        UserMovie expectedBody = new UserMovie(user, movie1, false);
        Map<String, Object> expectedMessage =  Map.of(
                "message", "Successfully updated watchlist item",
                "userId", userId,
                "movie_id", watchlistItemId,
                "association_object", expectedBody
        );

        Mockito.when(defaultUserService.updateWatchItemStatus(userId, watchlistItemId, true)).thenReturn(expectedBody);

        // When
        ResponseEntity<Object> result = userController.updateWatchItemStatus(userId, watchlistItemId, true);

        // Then
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.OK.value()));
        assertEquals(result.getBody(), expectedMessage);
    }

    @Test
    void failUpdateWatchList() throws UserNotFoundException, UserMovieNotFoundException {
        // Given
        Long userId = 1337L;
        Long watchlistItemId = 1L;

        // Attempt to update a watchlistitem that was never added
        UserMovieNotFoundException exception = new UserMovieNotFoundException(watchlistItemId);
        String expectedMessage =  "Could not update 'watched' status. Exception message: " + exception.getMessage();

        Mockito.when(defaultUserService.updateWatchItemStatus(userId, watchlistItemId, true)).thenThrow(exception);

        // When
        ResponseEntity<Object> result = userController.updateWatchItemStatus(userId, watchlistItemId, true);

        // Then
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.NOT_ACCEPTABLE.value()));
        assertEquals(result.getBody(), expectedMessage);
    }

    @Test
    void deleteUserSuccessfully() throws UserNotFoundException {
        Long userId = 1L;

        // No need to mock a return value since deleteUserById returns void
        Mockito.doNothing().when(defaultUserService).deleteUserById(userId);

        // When
        ResponseEntity<Object> result = userController.deleteUser(userId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody()); // Since NO_CONTENT implies no body

        // Verify the service method was called once
        Mockito.verify(defaultUserService, Mockito.times(1)).deleteUserById(userId);
    }

    @Test
    void deleteUserNotFound() throws UserNotFoundException {
        // Given
        Long userId = 999L;
        UserNotFoundException exception = new UserNotFoundException(userId);
        String expectedMessage = String.format("User with id '%d' does not exist", userId);

        Mockito.doThrow(exception).when(defaultUserService).deleteUserById(userId);

        // When
        ResponseEntity<Object> result = userController.deleteUser(userId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals(expectedMessage, result.getBody());

        // Verify the service method was called once
        Mockito.verify(defaultUserService, Mockito.times(1)).deleteUserById(userId);
    }
}
