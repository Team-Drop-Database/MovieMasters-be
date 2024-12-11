package movie_master.api.service;

import movie_master.api.exception.FriendshipNotFoundException;
import movie_master.api.model.Friendship;
import movie_master.api.model.User;
import movie_master.api.model.friendship.FriendshipStatus;
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

    @Mock private FriendshipRepository friendshipRepository;
    @InjectMocks private FriendshipService friendshipService;

    private User mockUser1;
    private User mockUser2;
    private Friendship mockFriendship;

    @BeforeEach
    void setup() {
        mockUser1 = new User("user1@gmail.com", "user1", "password1", "ROLE_USER", true);
        mockUser1.setUserId(1L);

        mockUser2 = new User("user2@gmail.com", "user2", "password2", "ROLE_USER", true);
        mockUser2.setUserId(2L);

        mockFriendship = new Friendship(mockUser1, mockUser2, FriendshipStatus.PENDING);
    }

    @Test
    void addFriendSuccessfully() throws FriendshipNotFoundException {
        // Arrange
        when(friendshipRepository.existsByUserAndFriend(mockUser1, mockUser2)).thenReturn(false);
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(mockFriendship);

        // Act
        Friendship result = friendshipService.addFriend(mockUser1, mockUser2);

        // Assert
        assertEquals(mockFriendship, result);
        verify(friendshipRepository, times(1)).save(any(Friendship.class));
    }

    @Test
    void addFriendAlreadyExists() {
        // Arrange
        when(friendshipRepository.existsByUserAndFriend(mockUser1, mockUser2)).thenReturn(true);

        // Act & Assert
        assertThrows(FriendshipNotFoundException.class, () -> friendshipService.addFriend(mockUser1, mockUser2));
        verify(friendshipRepository, never()).save(any(Friendship.class));
    }

    @Test
    void updateFriendshipStatusSuccessfully() throws FriendshipNotFoundException {
        // Arrange
        mockFriendship.setStatus(FriendshipStatus.REJECTED);
        when(friendshipRepository.findByUserAndFriend(mockUser2, mockUser1)).thenReturn(mockFriendship);
        when(friendshipRepository.save(mockFriendship)).thenReturn(mockFriendship);

        // Act
        Friendship result = friendshipService.updateFriendshipStatus(mockUser2, mockUser1, FriendshipStatus.REJECTED);

        // Assert
        assertEquals(FriendshipStatus.REJECTED, result.getStatus());
        verify(friendshipRepository, times(1)).save(mockFriendship);
    }

    @Test
    void updateFriendshipStatusNotFound() {
        // Arrange
        when(friendshipRepository.findByUserAndFriend(mockUser2, mockUser1)).thenReturn(null);

        // Act & Assert
        assertThrows(FriendshipNotFoundException.class, () -> friendshipService.updateFriendshipStatus(mockUser2, mockUser1, FriendshipStatus.ACCEPTED));
        verify(friendshipRepository, never()).save(any(Friendship.class));
    }

    @Test
    void getFriendsByStatusSuccessfully() {
        // Arrange
        List<Friendship> friendships = List.of(mockFriendship);
        when(friendshipRepository.findByUserAndStatus(mockUser1, FriendshipStatus.PENDING)).thenReturn(friendships);

        // Act
        List<Friendship> result = friendshipService.getFriendsByStatus(mockUser1, FriendshipStatus.PENDING);

        // Assert
        assertEquals(friendships, result);
        verify(friendshipRepository, times(1)).findByUserAndStatus(mockUser1, FriendshipStatus.PENDING);
    }

    @Test
    void deleteFriendSuccessfully() throws FriendshipNotFoundException {
        // Arrange
        when(friendshipRepository.findByUserAndFriend(mockUser1, mockUser2)).thenReturn(mockFriendship);

        // Act
        friendshipService.deleteFriend(mockUser1, mockUser2);

        // Assert
        verify(friendshipRepository, times(1)).delete(mockFriendship);
    }

    @Test
    void deleteFriendInBothDirectionsSuccessfully() throws FriendshipNotFoundException {
        // Arrange
        when(friendshipRepository.findByUserAndFriend(mockUser1, mockUser2)).thenReturn(null);
        when(friendshipRepository.findByUserAndFriend(mockUser2, mockUser1)).thenReturn(mockFriendship);

        // Act
        friendshipService.deleteFriend(mockUser1, mockUser2);

        // Assert
        verify(friendshipRepository, times(1)).delete(mockFriendship);
    }

    @Test
    void deleteFriendNotFound() {
        // Arrange
        when(friendshipRepository.findByUserAndFriend(mockUser1, mockUser2)).thenReturn(null);
        when(friendshipRepository.findByUserAndFriend(mockUser2, mockUser1)).thenReturn(null);

        // Act & Assert
        assertThrows(FriendshipNotFoundException.class, () -> friendshipService.deleteFriend(mockUser1, mockUser2));
        verify(friendshipRepository, never()).delete(any(Friendship.class));
    }

    @Test
    void getFriendshipSuccessfully() throws FriendshipNotFoundException {
        // Arrange
        when(friendshipRepository.findByUserAndFriend(mockUser1, mockUser2)).thenReturn(mockFriendship);

        // Act
        Friendship result = friendshipService.getFriendship(mockUser1, mockUser2);

        // Assert
        assertEquals(mockFriendship, result);
        verify(friendshipRepository, times(1)).findByUserAndFriend(mockUser1, mockUser2);
    }

    @Test
    void getFriendshipNotFound() {
        // Arrange
        when(friendshipRepository.findByUserAndFriend(mockUser1, mockUser2)).thenReturn(null);

        // Act & Assert
        assertThrows(FriendshipNotFoundException.class, () -> friendshipService.getFriendship(mockUser1, mockUser2));
        verify(friendshipRepository, times(1)).findByUserAndFriend(mockUser1, mockUser2);
    }
}
