package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import movie_master.api.dto.UserDto;
import movie_master.api.exception.*;
import movie_master.api.jwt.JwtUtil;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.request.UpdateUserRequest;
import movie_master.api.service.UserService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;
import java.util.*;

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
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Getting all users
     *
     * @return UserDto
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<UserDto> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Getting a user by their username
     *
     * @param username
     * @return UserDto
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Object> getUserByUsername(@PathVariable String username) {
        try {
            return ResponseEntity.ok().body(userService.getUserByUsername(username));
        } 
        catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Getting a user by their email
     *
     * @param email
     * @return UserDto
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Object> getUserByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok().body(userService.getUserByEmail(email));
        } 
        catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Change the role of the given user
     *
     * @param role
     * @param userId
     * @return updated user
     */
    @PutMapping("/{userId}/role")
    public ResponseEntity<Object> updateUserRole(@RequestBody String newRole,
                                                 @PathVariable Long userId,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        try {
            UserDto user = userService.updateUserRole(userId,
                    newRole,
                    jwtUtil.getRole(jwtToken.replace("Bearer ", "")));
            return ResponseEntity.ok().body(user);
        }
        catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Registers a new user
     *
     * @param httpServletRequest
     * @param registerUserRequest
     * @return userDto
     */
    @PostMapping
    public ResponseEntity<Object> register(HttpServletRequest httpServletRequest, @Valid @RequestBody RegisterUserRequest registerUserRequest) {
        try {
            UserDto userDto = userService.register(registerUserRequest);
            return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI())).body(userDto);
        }
        catch (EmailTakenException | UsernameTakenException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Update a user
     *
     * @param userId
     * @param updateUserRequest
     * @return the updated user
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @RequestBody UpdateUserRequest updateUserRequest,
                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        // TODO: validate if the user got the right permissions
        try {
            jwt = jwt.replace("Bearer ", "");
            UserDto user = userService.updateUser(userId,
                    updateUserRequest,
                    jwtUtil.getUserId(jwtToken),
                    jwtUtil.getRole(jwtToken));
            return ResponseEntity.ok(user);
        } 
        catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
        catch (EmailTakenException | UsernameTakenException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUserById(userId);
            return ResponseEntity.noContent().build();
        }
        catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
    public ResponseEntity<Object> getWatchList(@PathVariable Long userId) {
        try {
            Set<UserMovie> watchList = userService.getWatchList(userId);
            return ResponseEntity.ok(watchList);
        }
        catch (UserNotFoundException e) {
            // Return HTTP code with 404 error message if the user could not be found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Updates the watchlist of a user by adding a movie to it. Essentially an association
     *  ('MovieUser') is created between a user and a movie.
     * 
     * @param userId id of the user
     * @param movieId id of the movie
     * @return newly created watchitem (UserMovie)
     */
    @PutMapping("/{userId}/watchlist/add/{movieId}")
    public ResponseEntity<Object> addMovieToWatchlist(@PathVariable Long userId, @PathVariable Long movieId) {
        try {
            UserMovie watchItem = userService.addMovieToWatchlist(userId, movieId);
            return ResponseEntity.ok(Map.of(
                "message", "Successfully added to watchlist",
                "userId", userId,
                "movieId", movieId,
                "association_object", watchItem
                ));
        }
        catch(Exception exception) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body("Could not associate user with movie. Exception message: "
             + exception.getMessage());
        }
    }

    /**
     * Updates the watchlist of a user by removing a movie from it. This 
     * cuts the association ('MovieUser') between a User and a Movie.
     * 
     * @param userId id of a user
     * @param movieId id of a movie
     * @return message confirming the removal of the
     * movie from the users' watchlist
     */
    @PutMapping("/{userId}/watchlist/remove/{movieId}")
    public ResponseEntity<Object> removeMovieFromWatchlist(@PathVariable Long userId, @PathVariable Long movieId) {
        try {
            userService.removeMovieFromWatchlist(userId, movieId);
            return ResponseEntity.ok(Map.of(
                "message", "Successfully removed item from watchlist",
                "userId", userId,
                "movieId", movieId
                ));
        } catch(Exception exception) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body("Could not remove movie from watchlist. Exception message: "
             + exception.getMessage());
        }
    }

    /**
     * Updates the 'watched' status of an item on the watchlist.
     * 
     * @param userId id of the user
     * @param movieId id of the movie
     * @param watched whether the user has watched this movie or not
     * @return updated watchitem
     */
    @PutMapping("{userId}/watchlist/update/{movieId}")
    public ResponseEntity<Object> updateWatchItemStatus(@PathVariable Long userId, @PathVariable Long movieId,
     @RequestParam boolean watched) {
        try {
            UserMovie watchItem = userService.updateWatchItemStatus(userId, movieId, watched);
            return ResponseEntity.ok(Map.of(
                "message", "Successfully updated watchlist item",
                "userId", userId,
                "movie_id", movieId,
                "association_object", watchItem
                ));
        }
        catch(UserNotFoundException | UserMovieNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body("Could not update 'watched' status. Exception message: "
             + e.getMessage());
        }        
    }
}