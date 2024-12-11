package movie_master.api.controller;

import movie_master.api.exception.UnauthorizedFriendshipActionException;
import movie_master.api.jwt.JwtUtil;
import movie_master.api.model.Friendship;
import movie_master.api.model.User;
import movie_master.api.model.friendship.FriendshipStatus;
import movie_master.api.request.FriendshipRequest;
import movie_master.api.service.FriendshipService;
import movie_master.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendshipControllerTest {

    @Mock private FriendshipService friendshipService;
    @Mock private UserService userService;
    @Mock private JwtUtil jwtUtil;
    @InjectMocks private FriendshipController friendshipController;

    private String jwtTokenUser1;
    private String jwtTokenUser2;
    private User mockUser1;
    private User mockUser2;
    private User mockUser3;

    @BeforeEach
    void setup() {
        jwtTokenUser1 = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwidXNlcklkIjoxLCJzdWIiOiJ1c2VyMSIsI" +
                "mlhdCI6MTczMzc2Nzc4MCwiZXhwIjoxNzQxNTQzNzgwfQ.0yWfkHVkb9vzQi4Raq-VxNAsKBFuZWBRqC3bR0FgZWI";
        jwtTokenUser2 = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwidXNlcklkIjoyLCJzdWIiOiJ1c2VyMiIsImlh" +
                "dCI6MTczMzc2Nzc4MCwiZXhwIjoxNzQxNTQzNzgwfQ.xWFksILDJbDk8E7FXi1JEBuCkS43-G3OQgRhY2lQKkg";

        mockUser1 = new User("user1@gmail.com", "user1", "password1", "ROLE_USER", true);
        mockUser1.setUserId(1L);
        mockUser2 = new User("user2@gmail.com", "user2", "password2", "ROLE_USER", true);
        mockUser2.setUserId(2L);
        mockUser3 = new User("user3@gmail.com", "user3", "password3", "ROLE_USER", true);
        mockUser3.setUserId(3L);
    }

    @Test
    void addFriendSuccessfully() throws Exception {
        // Arrange
        Friendship friendship = new Friendship(mockUser1, mockUser2, FriendshipStatus.PENDING);
        when(jwtUtil.getUserId(jwtTokenUser1.replace("Bearer ", ""))).thenReturn(mockUser1.getUserId());
        when(userService.findUserById(mockUser1.getUserId())).thenReturn(mockUser1);
        when(userService.findUserByUsername(mockUser2.getUsername())).thenReturn(mockUser2);
        when(friendshipService.addFriend(mockUser1, mockUser2)).thenReturn(friendship);

        // Act
        ResponseEntity<Friendship> response = friendshipController.addFriend(
                new FriendshipRequest(mockUser2.getUsername(), FriendshipStatus.PENDING),
                jwtTokenUser1
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(friendship, response.getBody());
    }

    @Test
    void updateFriendshipStatusSuccessfully() throws Exception {
        // Arrange
        Friendship existingFriendship = new Friendship(mockUser1, mockUser2, FriendshipStatus.PENDING);
        Friendship updatedFriendship = new Friendship(mockUser1, mockUser2, FriendshipStatus.ACCEPTED);

        when(jwtUtil.getUserId(jwtTokenUser2.replace("Bearer ", ""))).thenReturn(mockUser2.getUserId());
        when(userService.findUserById(mockUser2.getUserId())).thenReturn(mockUser2);
        when(userService.findUserByUsername(mockUser1.getUsername())).thenReturn(mockUser1);

        when(friendshipService.getFriendship(mockUser1, mockUser2)).thenReturn(existingFriendship);
        when(friendshipService.updateFriendshipStatus(mockUser2, mockUser1, FriendshipStatus.ACCEPTED)).thenReturn(updatedFriendship);

        // Act
        ResponseEntity<Friendship> response = friendshipController.updateFriendshipStatus(
                new FriendshipRequest(mockUser1.getUsername(), FriendshipStatus.ACCEPTED),
                jwtTokenUser2
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedFriendship, response.getBody());
    }

    @Test
    void updateFriendshipStatusUnauthorized() throws Exception {
        // Arrange
        Friendship existingFriendship = new Friendship(mockUser1, mockUser2, FriendshipStatus.PENDING);

        when(jwtUtil.getUserId(jwtTokenUser1.replace("Bearer ", ""))).thenReturn(mockUser1.getUserId());
        when(userService.findUserById(mockUser1.getUserId())).thenReturn(mockUser1);
        when(userService.findUserByUsername(mockUser2.getUsername())).thenReturn(mockUser2);

        when(friendshipService.getFriendship(mockUser2, mockUser1)).thenReturn(existingFriendship);

        // Act & Assert
        UnauthorizedFriendshipActionException exception = assertThrows(
                UnauthorizedFriendshipActionException.class,
                () -> friendshipController.updateFriendshipStatus(
                        new FriendshipRequest(mockUser2.getUsername(), FriendshipStatus.ACCEPTED),
                        jwtTokenUser1
                )
        );

        assertEquals("User with id 1 is not authorized to update the friendship status with user 2.", exception.getMessage());
    }

    @Test
    void getFriendsByStatusSuccessfully() throws Exception {
        // Arrange
        List<Friendship> friends = List.of(
                new Friendship(mockUser1, mockUser2, FriendshipStatus.ACCEPTED),
                new Friendship(mockUser1, mockUser3, FriendshipStatus.ACCEPTED)
        );

        when(jwtUtil.getUserId(jwtTokenUser1.replace("Bearer ", ""))).thenReturn(mockUser1.getUserId());
        when(userService.findUserById(mockUser1.getUserId())).thenReturn(mockUser1);
        when(friendshipService.getFriendsByStatus(mockUser1, FriendshipStatus.ACCEPTED)).thenReturn(friends);

        // Act
        ResponseEntity<List<Friendship>> response = friendshipController.getFriendsByStatus(
                FriendshipStatus.ACCEPTED,
                jwtTokenUser1
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(friends, response.getBody());
    }

    @Test
    void deleteFriendSuccessfully() throws Exception {
        // Arrange
        when(jwtUtil.getUserId(jwtTokenUser1.replace("Bearer ", ""))).thenReturn(mockUser1.getUserId());
        when(userService.findUserById(mockUser1.getUserId())).thenReturn(mockUser1);
        when(userService.findUserByUsername(mockUser2.getUsername())).thenReturn(mockUser2);

        // Act
        friendshipController.deleteFriend(
                new FriendshipRequest(mockUser2.getUsername(), FriendshipStatus.ACCEPTED),
                jwtTokenUser1
        );

        // Assert
        verify(friendshipService, times(1)).deleteFriend(mockUser1, mockUser2);
    }
}