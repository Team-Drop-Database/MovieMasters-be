package movie_master.api.service;

import movie_master.api.dto.UserDto;
import movie_master.api.exception.FriendshipNotFoundException;
import movie_master.api.mapper.UserDtoMapper;
import movie_master.api.model.Friendship;
import movie_master.api.model.User;
import movie_master.api.model.friendship.FriendshipStatus;
import movie_master.api.model.role.Role;
import movie_master.api.repository.FriendshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;
    @InjectMocks
    private FriendshipService friendshipService;

    private UserDto mockUserDto1;
    private UserDto mockUserDto2;
    private Friendship mockFriendship;

    @BeforeEach
    void setup() {
        UserDtoMapper userDtoMapper = new UserDtoMapper();

        User mockUser1 = new User("user1@gmail.com", "user1", "password1", Role.ROLE_USER, true);
        mockUser1.setUserId(1L);

        User mockUser2 = new User("user2@gmail.com", "user2", "password2", Role.ROLE_USER, true);
        mockUser2.setUserId(2L);

        mockUserDto1 = userDtoMapper.apply(mockUser1);
        mockUserDto2 = userDtoMapper.apply(mockUser2);
        mockFriendship = new Friendship(mockUser1, mockUser2, FriendshipStatus.PENDING);
    }

    @Test
    void addFriendSuccessfully() throws FriendshipNotFoundException {
        // Arrange
        when(friendshipRepository.existsByUserAndFriend(any(User.class), any(User.class))).thenReturn(false);
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(mockFriendship);

        // Act
        Friendship result = friendshipService.addFriend(mockUserDto1, mockUserDto2);

        // Assert
        assertEquals(mockFriendship, result);
        verify(friendshipRepository, times(1)).save(any(Friendship.class));
    }

    @Test
    void addFriendAlreadyExists() {
        // Arrange
        when(friendshipRepository.existsByUserAndFriend(any(User.class), any(User.class))).thenReturn(true);

        // Act & Assert
        assertThrows(FriendshipNotFoundException.class, () -> friendshipService.addFriend(mockUserDto1, mockUserDto2));
        verify(friendshipRepository, never()).save(any(Friendship.class));
    }

    @Test
    void updateFriendshipStatusSuccessfully() throws FriendshipNotFoundException {
        // Arrange
        mockFriendship.setStatus(FriendshipStatus.REJECTED);
        when(friendshipRepository.findByUserAndFriend(any(User.class), any(User.class))).thenReturn(mockFriendship);
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(mockFriendship);

        // Act
        Friendship result = friendshipService.updateFriendshipStatus(mockUserDto2, mockUserDto1, FriendshipStatus.REJECTED);

        // Assert
        assertEquals(FriendshipStatus.REJECTED, result.getStatus());
        verify(friendshipRepository, times(1)).save(any(Friendship.class));
    }

    @Test
    void updateFriendshipStatusNotFound() {
        // Arrange
        when(friendshipRepository.findByUserAndFriend(any(User.class), any(User.class))).thenReturn(null);

        // Act & Assert
        assertThrows(FriendshipNotFoundException.class, () -> friendshipService.updateFriendshipStatus(mockUserDto2, mockUserDto1, FriendshipStatus.ACCEPTED));
        verify(friendshipRepository, never()).save(any(Friendship.class));
    }

    @Test
    void getFriendsByStatusSuccessfully() {
        // Arrange
        List<Friendship> friendships = List.of(mockFriendship);
        when(friendshipRepository.findByUserAndStatus(any(User.class), eq(FriendshipStatus.PENDING))).thenReturn(friendships);

        // Act
        List<Friendship> result = friendshipService.getFriendsByStatus(mockUserDto1, FriendshipStatus.PENDING);

        // Assert
        assertEquals(friendships, result);
        verify(friendshipRepository, times(1)).findByUserAndStatus(any(User.class), eq(FriendshipStatus.PENDING));
    }

    @Test
    void deleteFriendSuccessfully() throws FriendshipNotFoundException {
        // Arrange
        when(friendshipRepository.findByUserAndFriend(any(User.class), any(User.class))).thenReturn(mockFriendship);

        // Act
        friendshipService.deleteFriend(mockUserDto1, mockUserDto2);

        // Assert
        verify(friendshipRepository, times(1)).delete(any(Friendship.class));
    }

    @Test
    void deleteFriendNotFound() {
        // Arrange
        when(friendshipRepository.findByUserAndFriend(any(User.class), any(User.class))).thenReturn(null);

        // Act & Assert
        assertThrows(FriendshipNotFoundException.class, () -> friendshipService.deleteFriend(mockUserDto1, mockUserDto2));
        verify(friendshipRepository, never()).delete(any(Friendship.class));
    }


    @Test
    void getFriendshipSuccessfully() throws FriendshipNotFoundException {
        // Arrange
        when(friendshipRepository.findByUserAndFriend(any(User.class), any(User.class))).thenReturn(mockFriendship);

        // Act
        Friendship result = friendshipService.getFriendship(mockUserDto1, mockUserDto2);

        // Assert
        assertEquals(mockFriendship, result);
        verify(friendshipRepository, times(1)).findByUserAndFriend(any(User.class), any(User.class));
    }

    @Test
    void getFriendshipNotFound() {
        // Arrange
        when(friendshipRepository.findByUserAndFriend(any(User.class), any(User.class))).thenReturn(null);

        // Act & Assert
        assertThrows(FriendshipNotFoundException.class, () -> friendshipService.getFriendship(mockUserDto1, mockUserDto2));
        verify(friendshipRepository, times(1)).findByUserAndFriend(any(User.class), any(User.class));
    }
}
