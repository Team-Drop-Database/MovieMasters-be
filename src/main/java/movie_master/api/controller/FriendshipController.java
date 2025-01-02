package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import movie_master.api.dto.FriendshipDto;
import movie_master.api.exception.FriendshipAlreadyExistsException;
import movie_master.api.exception.FriendshipNotFoundException;
import movie_master.api.exception.UserCannotFriendThemself;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.jwt.JwtUtil;
import movie_master.api.model.friendship.FriendshipStatus;
import movie_master.api.request.FriendshipRequest;
import movie_master.api.service.DefaultFriendshipService;
import movie_master.api.service.FriendshipService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/friends")
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final JwtUtil jwtUtil;

    public FriendshipController(DefaultFriendshipService friendshipService, JwtUtil jwtUtil) {
        this.friendshipService = friendshipService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<Object> addFriend(@Valid @RequestBody FriendshipRequest request,
                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt, HttpServletRequest httpServletRequest) {
        try {
            Long userId = jwtUtil.getUserId(jwt.replace("Bearer ", ""));

            FriendshipDto friendshipDto = friendshipService.addFriend(userId, request.username());
            return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI())).body(friendshipDto);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FriendshipAlreadyExistsException | UserCannotFriendThemself e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<Object> updateFriendshipStatus(@Valid @RequestBody FriendshipRequest request,
                                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        try {
            Long userId = jwtUtil.getUserId(jwt.replace("Bearer ", ""));
            FriendshipStatus status;

            if (request.status() != null) {
                try {
                    status = FriendshipStatus.valueOf(request.status().toUpperCase());
                } catch (IllegalArgumentException ex) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid friendship status provided.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Friendship status cannot be null.");
            }

            FriendshipDto friendshipDto = friendshipService.updateFriendshipStatus(request.username(), userId, status);
            return ResponseEntity.ok(friendshipDto);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FriendshipNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Object> getFriendsByStatus(@RequestParam FriendshipStatus status,
                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        Long userId = jwtUtil.getUserId(jwt.replace("Bearer ", ""));

        List<FriendshipDto> friends = friendshipService.getFriendsByStatus(userId, status);
        return ResponseEntity.ok(friends);
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteFriend(@Valid @RequestBody FriendshipRequest request,
                                               @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        try {
            Long userId = jwtUtil.getUserId(jwt.replace("Bearer ", ""));

            friendshipService.deleteFriend(userId, request.username());
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FriendshipNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
