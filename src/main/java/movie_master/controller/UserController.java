package movie_master.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import movie_master.api.UserNotFoundException;
import movie_master.model.UserMovie;
import movie_master.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/watchlist")
    public ResponseEntity<?> getWatchList(@RequestParam(required = true) Long userId) {
        try {
            Set<UserMovie> watchList = userService.getWatchList(userId);
            return ResponseEntity.ok(watchList);
        } catch (UserNotFoundException exception) {
            // Return HTTP code with 404 error message if the user could not be found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("User not found: " + exception.getMessage());
        }
    }
}
