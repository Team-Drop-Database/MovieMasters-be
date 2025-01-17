package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import movie_master.api.dto.UserDto;
import movie_master.api.dto.UserMovie.UserMovieDto;
import movie_master.api.exception.*;
import movie_master.api.jwt.JwtUtil;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.model.role.Role;
import movie_master.api.request.PasswordResetRequest;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.request.UpdateUserRequest;
import movie_master.api.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Getting a user by their username
     *
     * @param username username of the user to find
     * @return UserDto
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Object> getUserByUsername(@PathVariable String username) {
        try {
            return ResponseEntity.ok().body(userService.getUserByUsername(username));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Getting a user by their email
     *
     * @param email email of the user to find
     * @return UserDto
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Object> getUserByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok().body(userService.getUserByEmail(email));
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Change the role of the given user
     *
     * @param newRole role to give the user
     * @param userId  ID of the user to give the role to
     * @return updated user
     */
    @PutMapping("/{userId}/role")
    public ResponseEntity<Object> updateUserRole(@RequestBody String newRole,
                                                 @PathVariable Long userId,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        try {
            UserDto user = userService.updateUserRole(userId,
                    newRole,
                    jwtUtil.getRole(jwt.replace("Bearer ", "")));
            return ResponseEntity.ok().body(generateTokens(user.id(), user.username(), user.role(), user.profile_picture()));
        } catch (UserNotFoundException e) {
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
        } catch (EmailTakenException | UsernameTakenException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Update a user
     *
     * @param userId            ID of the user to update
     * @param updateUserRequest object with the values to update
     * @return the updated user
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @RequestBody UpdateUserRequest updateUserRequest,
                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        try {
            jwt = jwt.replace("Bearer ", "");
            UserDto user = userService.updateUser(userId,
                    updateUserRequest,
                    jwtUtil.getUserId(jwt),
                    jwtUtil.getRole(jwt));
            return ResponseEntity.ok(generateTokens(user.id(), user.username(), user.role(), user.profile_picture()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (EmailTakenException | UsernameTakenException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Delete a user
     *
     * @param userId ID of the user to delete
     * @return - 204 no content if the user is succesfully deleted
     *         - 404 not found if the user with the given ID does not exist
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUserById(userId);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Retrieve the watchlist of a given user.
     *
     * @param userId id of the user
     * @return a list of objects containing data such as the
     * users opinion about the movie along with information
     * about the movie itself.
     */
    @GetMapping("/{userId}/watchlist")
    public ResponseEntity<Object> getWatchList(@PathVariable Long userId) {
        try {
            Set<UserMovieDto> watchList = userService.getWatchList(userId);
            return ResponseEntity.ok(watchList);
        } catch (UserNotFoundException e) {
            // Return HTTP code with 404 error message if the user could not be found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Retrieves the watchlist item of a user by the given movie
     *
     * @param userId id of the user
     * @param movieId id of the movie
     * @return UserMovieDto
     */
    @GetMapping("/{userId}/watchlist/movie/{movieId}")
    public ResponseEntity<Object> getWatchListItem(@PathVariable Long userId, @PathVariable Long movieId) {
        try {
            UserMovieDto watchList = userService.getWatchListItem(userId, movieId);

            if (watchList != null) {
                return ResponseEntity.ok(watchList);
            }
            return ResponseEntity.notFound().build();
        } catch (UserNotFoundException e) {
            // Return HTTP code with 404 error message if the user could not be found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Updates the watchlist of a user by adding a movie to it. Essentially an association
     * ('MovieUser') is created between a user and a movie.
     *
     * @param userId  id of the user
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
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("Could not associate user with movie. Exception message: "
                            + exception.getMessage());
        }
    }

    /**
     * Updates the watchlist of a user by removing a movie from it. This
     * cuts the association ('MovieUser') between a User and a Movie.
     *
     * @param userId  id of a user
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
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("Could not remove movie from watchlist. Exception message: "
                            + exception.getMessage());
        }
    }

    /**
     * Updates the 'watched' status of an item on the watchlist.
     *
     * @param userId  id of the user
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
        } catch (UserNotFoundException | UserMovieNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("Could not update 'watched' status. Exception message: "
                            + e.getMessage());
        }
    }

    /**
     * 
     * @param userId id of the user
     * @param banned boolean value representing the new 
     * banned-status of the user
     * @return a response message indicating succes/failure of updating the 
     * users' banned status, along with the updated user object.
     */
    @PutMapping("{userId}/banstatus")
    public ResponseEntity<Object> updateUserBannedStatus(@PathVariable Long userId, @RequestParam boolean banned){
        try {
            User updatedUser = userService.updateUserBannedStatus(userId, banned);
            return ResponseEntity.ok(Map.of(
                "message", "Succesfully changed banned status",
                "userId", userId,
                "banned_status", updatedUser.isBanned(),
                "user_object", updatedUser
            ));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("Could not update 'banned' status. Exception message: "
                            + e.getMessage());
        }
    }

    @PostMapping("/password-reset")
    public ResponseEntity<Object> requestPasswordReset(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody PasswordResetRequest passwordResetTokenRequest) {
        try {
            userService.requestPasswordReset(passwordResetTokenRequest.email());

            return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI()))
                    .body("Instructions for resetting your password have been sent");
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UserAlreadyHasPasswordResetToken e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // change-password
    @PutMapping("/password-reset")
    public ResponseEntity<Object> resetPassword(@RequestParam String passwordResetToken) {
        return null;
    }

    /**
     * Generate jwt and refresh token
     *
     * @param userId   ID of the user
     * @param username username of the user
     * @param role     role of the user
     * @return JWT token and JWT refresh token
     */
    private Map<String, String> generateTokens(Long userId, String username, Role role, String profileUrl) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("profileUrl", profileUrl);

        String jwt = jwtUtil.generateJwt(claims, username);
        String refreshJwt = jwtUtil.generateRefreshJwt(claims, username);

        return Map.of("accessToken", jwt, "refreshToken", refreshJwt);
    }
}