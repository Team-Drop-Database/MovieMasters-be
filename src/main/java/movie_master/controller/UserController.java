package movie_master.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import movie_master.model.Movie;
import movie_master.model.UserMovie;
import movie_master.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/watchlist")
    public Set<UserMovie> getWatchList(@RequestParam(required = true) int userId){
        return userService.getWatchList(userId);
    }

}
