package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import movie_master.api.dto.FriendshipDto;
import movie_master.api.exception.FriendshipAlreadyExistsException;
import movie_master.api.exception.FriendshipNotFoundException;
import movie_master.api.exception.UserCannotFriendThemself;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.jwt.JwtUtil;
import movie_master.api.model.User;
import movie_master.api.model.friendship.FriendshipStatus;
import movie_master.api.model.role.Role;
import movie_master.api.request.FriendshipRequest;
import movie_master.api.service.DefaultFriendshipService;
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

    @Mock private DefaultFriendshipService defaultFriendshipService;
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

        mockUser1 = new User("user1@gmail.com", "user1", "password1", Role.ROLE_USER, true, false);
        mockUser1.setUserId(1L);
        mockUser2 = new User("user2@gmail.com", "user2", "password2", Role.ROLE_USER, true, false);
        mockUser2.setUserId(2L);
        mockUser3 = new User("user3@gmail.com", "user3", "password3", Role.ROLE_USER, true, false);
        mockUser3.setUserId(3L);
    }

    @Test
    void addFriendSuccessfully() throws UserNotFoundException, FriendshipAlreadyExistsException, UserCannotFriendThemself {
        // Arrange
        FriendshipDto friendshipDto = new FriendshipDto(mockUser1.getUserId(), null, mockUser2.getUsername(), null,
                FriendshipStatus.PENDING, LocalDateTime.now());
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        when(jwtUtil.getUserId(jwtTokenUser1.replace("Bearer ", ""))).thenReturn(mockUser1.getUserId());
        when(defaultFriendshipService.addFriend(mockUser1.getUserId(), mockUser2.getUsername())).thenReturn(friendshipDto);
        when(mockRequest.getRequestURI()).thenReturn("/friends");

        // Act
        ResponseEntity<Object> response = friendshipController.addFriend(
                new FriendshipRequest(mockUser2.getUsername(), FriendshipStatus.PENDING.toString()),
                jwtTokenUser1, mockRequest
        );

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(friendshipDto, response.getBody());
    }

    @Test
    void updateFriendshipStatusSuccessfully() throws UserNotFoundException, FriendshipNotFoundException {
        // Arrange
        FriendshipDto updatedFriendshipDto = new FriendshipDto(mockUser1.getUserId(), mockUser1.getUsername(), mockUser2.getUsername(), null,
                FriendshipStatus.ACCEPTED, LocalDateTime.now());
        when(jwtUtil.getUserId(jwtTokenUser2.replace("Bearer ", ""))).thenReturn(mockUser2.getUserId());
        when(defaultFriendshipService.updateFriendshipStatus(mockUser1.getUsername(), mockUser2.getUserId(), FriendshipStatus.ACCEPTED)).thenReturn(updatedFriendshipDto);

        // Act
        ResponseEntity<Object> response = friendshipController.updateFriendshipStatus(
                new FriendshipRequest(mockUser1.getUsername(), FriendshipStatus.ACCEPTED.toString()),
                jwtTokenUser2
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedFriendshipDto, response.getBody());
    }

    @Test
    void updateFriendshipStatusWhenUpdatingOwnRequest() throws UserNotFoundException, FriendshipNotFoundException {
        // Arrange
        FriendshipRequest friendshipRequest = new FriendshipRequest(mockUser1.getUsername(), FriendshipStatus.ACCEPTED.toString());
        when(jwtUtil.getUserId(jwtTokenUser2.replace("Bearer ", ""))).thenReturn(mockUser2.getUserId());
        when(defaultFriendshipService.updateFriendshipStatus(mockUser1.getUsername(), mockUser2.getUserId(), FriendshipStatus.ACCEPTED))
                .thenThrow(new FriendshipNotFoundException(mockUser2.getUserId(), mockUser1.getUserId()));

        // Act
        ResponseEntity<Object> response = friendshipController.updateFriendshipStatus(
                friendshipRequest,
                jwtTokenUser2
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Friendship between user with id: 2 and user with id: 1 does not exist", response.getBody());
    }

    @Test
    void getFriendsByStatusSuccessfully() {
        // Arrange
        List<FriendshipDto> friends = List.of(
                new FriendshipDto(1L, mockUser1.getUsername(), mockUser2.getUsername(), null, FriendshipStatus.ACCEPTED, LocalDateTime.now()),
                new FriendshipDto(2L, mockUser2.getUsername(), mockUser3.getUsername(), null, FriendshipStatus.ACCEPTED, LocalDateTime.now())
        );
        when(jwtUtil.getUserId(jwtTokenUser1.replace("Bearer ", ""))).thenReturn(mockUser1.getUserId());
        when(defaultFriendshipService.getFriendsByStatus(mockUser1.getUserId(), FriendshipStatus.ACCEPTED)).thenReturn(friends);

        // Act
        ResponseEntity<Object> response = friendshipController.getFriendsByStatus(
                FriendshipStatus.ACCEPTED,
                jwtTokenUser1
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(friends, response.getBody());
    }

    @Test
    void deleteFriendSuccessfully() throws UserNotFoundException, FriendshipNotFoundException {
        // Arrange
        when(jwtUtil.getUserId(jwtTokenUser1.replace("Bearer ", ""))).thenReturn(mockUser1.getUserId());

        // Act
        ResponseEntity<Object> response = friendshipController.deleteFriend(
                new FriendshipRequest(mockUser2.getUsername(), FriendshipStatus.ACCEPTED.toString()),
                jwtTokenUser1
        );

        // Assert
        verify(defaultFriendshipService, times(1)).deleteFriend(mockUser1.getUserId(), mockUser2.getUsername());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
