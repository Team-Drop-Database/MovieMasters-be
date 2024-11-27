package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import movie_master.api.dto.UserDto;
import movie_master.api.exception.EmailTakenException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.exception.UsernameTakenException;
import movie_master.api.model.UserMovie;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.service.DefaultUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;

/**
 * Controller for users
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final DefaultUserService defaultUserService;

    public UserController(DefaultUserService defaultUserService) {
        this.defaultUserService = defaultUserService;
    }

    @PostMapping
    ResponseEntity<Object> register(HttpServletRequest httpServletRequest, @Valid @RequestBody RegisterUserRequest registerUserRequest) {
        try {
            UserDto userDto = defaultUserService.register(registerUserRequest);
            return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI())).body(userDto);
        }
        catch (EmailTakenException | UsernameTakenException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/watchlist")
    ResponseEntity<Object> getWatchList(@RequestParam(required = true) Long userId) {
        try {
            Set<UserMovie> watchList = defaultUserService.getWatchList(userId);
            return ResponseEntity.ok(watchList);
        } catch (UserNotFoundException exception) {
            // Return HTTP code with 404 error message if the user could not be found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User not found: " + exception.getMessage());
        }
    }
}