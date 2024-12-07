package movie_master.api.controller;

import movie_master.api.model.Friendship;
import movie_master.api.model.User;
import movie_master.api.model.friendship.FriendshipStatus;
import movie_master.api.request.FriendshipRequest;
import movie_master.api.service.FriendshipService;
import movie_master.api.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendships")
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final UserService userService;

    public FriendshipController(FriendshipService friendshipService, UserService userService) {
        this.friendshipService = friendshipService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public Friendship addFriend(@RequestBody FriendshipRequest request) {
        User user = new User();
        user.setId(request.getUserId());

        User friend = new User();
        friend.setId(request.getFriendId());

        return friendshipService.addFriend(user, friend);
    }

    @PutMapping("/update")
    public Friendship updateFriendshipStatus(@RequestBody FriendshipRequest request) {
        User user = new User();
        user.setId(request.getUserId());

        User friend = new User();
        friend.setId(request.getFriendId());

        return friendshipService.updateFriendshipStatus(user, friend, request.getStatus());
    }

    @GetMapping("/{userId}/friends")
    public List<Friendship> getFriendsByStatus(@PathVariable Long userId, @RequestParam FriendshipStatus status) {
        User user = new User();
        user.setId(userId);

        return friendshipService.getFriendsByStatus(user, status);
    }

    @DeleteMapping("/remove")
    public void deleteFriend(@RequestBody FriendshipRequest request) {
        User user = new User();
        user.setId(request.getUserId());

        User friend = new User();
        friend.setId(request.getFriendId());

        friendshipService.deleteFriend(user, friend);
    }
}
