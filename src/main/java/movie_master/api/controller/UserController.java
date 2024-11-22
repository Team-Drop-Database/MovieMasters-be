package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import movie_master.api.dto.UserDto;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.service.UserService;
import movie_master.api.exception.EmailHasAlreadyBeenTaken;
import movie_master.api.exception.UsernameHasAlreadyBeenTaken;
import movie_master.api.exception.UserNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import movie_master.api.model.UserMovie;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller for users
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user
     * 
     * @param httpServletRequest
     * @param registerUserRequest
     * @return data providing information about the new user.
     */
    @PostMapping
    private ResponseEntity<Object> register(HttpServletRequest httpServletRequest, @Valid @RequestBody RegisterUserRequest registerUserRequest) {
        try {
            UserDto userDto = userService.register(registerUserRequest);
            return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI())).body(userDto);
        }
        catch (EmailHasAlreadyBeenTaken | UsernameHasAlreadyBeenTaken e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieve the watchlist of a given user.
     * 
     * @param userId id of the user
     * @return  a list of objects containing data such as the
     *  users opinion about the movie along with information 
     * about the movie itself.
     */
    @GetMapping("/{userId}/watchlist")
    public ResponseEntity<?> getWatchList(@PathVariable Long userId) {
        try {
            Set<UserMovie> watchList = userService.getWatchList(userId);
            return ResponseEntity.ok(watchList);
        } catch (UserNotFoundException exception) {
            // Return HTTP code with 404 error message if the user could not be found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("User not found: " + exception.getMessage());
        }
    }

    /**
     * Updates the watchlist of a movie by adding a movie to it. Essentially an association
     *  ('MovieUser') is created between a user and a movie.
     * 
     * @param userId id of the user
     * @param movieId id of the movie
     * @return newly created watchitem (UserMovie)
     */
    @PutMapping("/watchlist/add/{userId}/{movieId}")
    public ResponseEntity<?> addMovieToWatchlist(@PathVariable Long userId, @PathVariable Long movieId) {
        try {
            UserMovie watchItem = userService.addMovieToWatchlist(userId, movieId);
            return ResponseEntity.ok(Map.of(
                "message", "Successfully added to watchlist",
                "userId", userId,
                "movie_id", movieId,
                "association_object", watchItem
                ));
        } catch(Exception exception) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body("Could not associate user with movie. Exception message: "
             + exception.getMessage());
        }
    }
}