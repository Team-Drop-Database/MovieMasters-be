package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import movie_master.api.dto.UserDto;
import movie_master.api.exception.EmailTakenException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.exception.UsernameTakenException;
import movie_master.api.model.Movie;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        UserDto expectedBody = new UserDto(1L, "email", "username", "ugly", LocalDate.now(), "some");

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
        User user = new User("example@test.mail", "User McNameface", "password1234", "QA", true);
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
}
