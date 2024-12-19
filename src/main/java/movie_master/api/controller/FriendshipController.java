package movie_master.api.controller;

import movie_master.api.dto.FriendshipDto;
import movie_master.api.dto.UserDto;
import movie_master.api.exception.FriendshipNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.jwt.JwtUtil;
import movie_master.api.model.Friendship;
import movie_master.api.model.friendship.FriendshipStatus;
import movie_master.api.request.FriendshipRequest;
import movie_master.api.service.FriendshipService;
import movie_master.api.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendships")
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public FriendshipController(FriendshipService friendshipService, UserService userService, JwtUtil jwtUtil) {
        this.friendshipService = friendshipService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/add")
    public ResponseEntity<Friendship> addFriend(@RequestBody FriendshipRequest request,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken) throws UserNotFoundException, FriendshipNotFoundException {
        Long userId = jwtUtil.getUserId(jwtToken.replace("Bearer ", ""));
        UserDto userDto = userService.getUserById(userId);
        UserDto friendDto = userService.getUserByUsername(request.username());

        Friendship friendship = friendshipService.addFriend(userDto, friendDto);
        return ResponseEntity.ok(friendship);
    }

    @PutMapping("/update")
    public ResponseEntity<Friendship> updateFriendshipStatus(@RequestBody FriendshipRequest request,
                                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken) throws UserNotFoundException, FriendshipNotFoundException {
        Long userId = jwtUtil.getUserId(jwtToken.replace("Bearer ", ""));
        UserDto userDto = userService.getUserById(userId);
        UserDto friendDto = userService.getUserByUsername(request.username());

        Friendship friendship = friendshipService.updateFriendshipStatus(userDto, friendDto, request.status());
        return ResponseEntity.ok(friendship);
    }

    @GetMapping("/friends")
    public ResponseEntity<List<FriendshipDto>> getFriendsByStatus(@RequestParam FriendshipStatus status,
                                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken) throws UserNotFoundException {
        Long userId = jwtUtil.getUserId(jwtToken.replace("Bearer ", ""));
        UserDto userDto = userService.getUserById(userId);

        List<FriendshipDto> friends = friendshipService.getFriendsByStatus(userDto, status);
        return ResponseEntity.ok(friends);
    }

    @DeleteMapping("/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(@RequestBody FriendshipRequest request,
                             @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken) throws UserNotFoundException, FriendshipNotFoundException {
        Long userId = jwtUtil.getUserId(jwtToken.replace("Bearer ", ""));
        UserDto userDto = userService.getUserById(userId);
        UserDto friendDto = userService.getUserByUsername(request.username());

        friendshipService.deleteFriend(userDto, friendDto);
    }
}
