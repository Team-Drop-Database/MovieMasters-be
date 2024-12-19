package movie_master.api.controller;

import movie_master.api.dto.FriendshipDto;
import movie_master.api.dto.UserDto;
import movie_master.api.jwt.JwtUtil;
import movie_master.api.mapper.UserDtoMapper;
import movie_master.api.model.Friendship;
import movie_master.api.model.User;
import movie_master.api.model.friendship.FriendshipStatus;
import movie_master.api.model.role.Role;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    private UserDto mockUserDto1;
    private UserDto mockUserDto2;
    @BeforeEach
    void setup() {
        UserDtoMapper userDtoMapper = new UserDtoMapper();

        jwtTokenUser1 = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwidXNlcklkIjoxLCJzdWIiOiJ1c2VyMSIsI" +
                "mlhdCI6MTczMzc2Nzc4MCwiZXhwIjoxNzQxNTQzNzgwfQ.0yWfkHVkb9vzQi4Raq-VxNAsKBFuZWBRqC3bR0FgZWI";
        jwtTokenUser2 = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwidXNlcklkIjoyLCJzdWIiOiJ1c2VyMiIsImlh" +
                "dCI6MTczMzc2Nzc4MCwiZXhwIjoxNzQxNTQzNzgwfQ.xWFksILDJbDk8E7FXi1JEBuCkS43-G3OQgRhY2lQKkg";

        mockUser1 = new User("user1@gmail.com", "user1", "password1", Role.ROLE_USER, true);
        mockUser1.setUserId(1L);
        mockUser2 = new User("user2@gmail.com", "user2", "password2", Role.ROLE_USER, true);
        mockUser2.setUserId(2L);
        mockUser3 = new User("user3@gmail.com", "user3", "password3", Role.ROLE_USER, true);
        mockUser3.setUserId(3L);

        mockUserDto1 = userDtoMapper.apply(mockUser1);
        mockUserDto2 = userDtoMapper.apply(mockUser2);
    }

    @Test
    void addFriendSuccessfully() throws Exception {
        // Arrange
        Friendship friendship = new Friendship(mockUser1, mockUser2, FriendshipStatus.PENDING);
        when(jwtUtil.getUserId(jwtTokenUser1.replace("Bearer ", ""))).thenReturn(mockUserDto1.id());
        when(userService.getUserById(mockUserDto1.id())).thenReturn(mockUserDto1);
        when(userService.getUserByUsername(mockUser2.getUsername())).thenReturn(mockUserDto2);
        when(friendshipService.addFriend(mockUserDto1, mockUserDto2)).thenReturn(friendship);

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
        Friendship updatedFriendship = new Friendship(mockUser1, mockUser2, FriendshipStatus.ACCEPTED);

        when(jwtUtil.getUserId(jwtTokenUser2.replace("Bearer ", ""))).thenReturn(mockUserDto2.id());
        when(userService.getUserById(mockUserDto2.id())).thenReturn(mockUserDto2);
        when(userService.getUserByUsername(mockUser1.getUsername())).thenReturn(mockUserDto1);
        when(friendshipService.updateFriendshipStatus(mockUserDto2, mockUserDto1, FriendshipStatus.ACCEPTED)).thenReturn(updatedFriendship);

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
    void getFriendsByStatusSuccessfully() throws Exception {
        // Arrange
        List<FriendshipDto> friends = List.of(
                new FriendshipDto(1L, mockUser2.getUsername(), FriendshipStatus.ACCEPTED, LocalDateTime.now()),
                new FriendshipDto(2L, mockUser3.getUsername(), FriendshipStatus.ACCEPTED, LocalDateTime.now())
        );

        when(jwtUtil.getUserId(jwtTokenUser1.replace("Bearer ", ""))).thenReturn(mockUserDto1.id());
        when(userService.getUserById(mockUserDto1.id())).thenReturn(mockUserDto1);
        when(friendshipService.getFriendsByStatus(mockUserDto1, FriendshipStatus.ACCEPTED)).thenReturn(friends);

        // Act
        ResponseEntity<List<FriendshipDto>> response = friendshipController.getFriendsByStatus(
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
        when(jwtUtil.getUserId(jwtTokenUser1.replace("Bearer ", ""))).thenReturn(mockUserDto1.id());
        when(userService.getUserById(mockUserDto1.id())).thenReturn(mockUserDto1);
        when(userService.getUserByUsername(mockUser2.getUsername())).thenReturn(mockUserDto2);

        // Act
        friendshipController.deleteFriend(
                new FriendshipRequest(mockUser2.getUsername(), FriendshipStatus.ACCEPTED),
                jwtTokenUser1
        );

        // Assert
        verify(friendshipService, times(1)).deleteFriend(mockUserDto1, mockUserDto2);
    }
}
