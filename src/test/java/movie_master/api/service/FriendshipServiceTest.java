package movie_master.api.service;

import movie_master.api.dto.FriendshipDto;
import movie_master.api.dto.UserDto;
import movie_master.api.exception.FriendshipNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.mapper.UserDtoMapper;
import movie_master.api.model.Friendship;
import movie_master.api.model.User;
import movie_master.api.model.friendship.FriendshipStatus;
import movie_master.api.model.role.Role;
import movie_master.api.repository.FriendshipRepository;
import movie_master.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendshipService friendshipService;

    private UserDto mockUserDto1;
    private UserDto mockUserDto2;
    private Friendship mockFriendship;
    private FriendshipDto mockFriendshipDto;

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
        mockFriendshipDto = new FriendshipDto(1L, mockUser2.getUsername(), FriendshipStatus.PENDING, LocalDateTime.now());
    }

    @Test
    void addFriendSuccessfully() throws FriendshipNotFoundException, UserNotFoundException {
        // Arrange
        when(userRepository.findById(mockUserDto1.id())).thenReturn(java.util.Optional.of(new User("user1@gmail.com", "user1", "password1", Role.ROLE_USER, true)));
        when(userRepository.findById(mockUserDto2.id())).thenReturn(java.util.Optional.of(new User("user2@gmail.com", "user2", "password2", Role.ROLE_USER, true)));
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
        when(userRepository.findById(mockUserDto1.id())).thenReturn(java.util.Optional.of(new User("user1@gmail.com", "user1", "password1", Role.ROLE_USER, true)));
        when(userRepository.findById(mockUserDto2.id())).thenReturn(java.util.Optional.of(new User("user2@gmail.com", "user2", "password2", Role.ROLE_USER, true)));
        when(friendshipRepository.existsByUserAndFriend(any(User.class), any(User.class))).thenReturn(true);

        // Act & Assert
        assertThrows(FriendshipNotFoundException.class, () -> friendshipService.addFriend(mockUserDto1, mockUserDto2));
        verify(friendshipRepository, never()).save(any(Friendship.class));
    }

    @Test
    void updateFriendshipStatusSuccessfully() throws FriendshipNotFoundException, UserNotFoundException {
        // Arrange
        when(userRepository.findById(mockUserDto1.id())).thenReturn(java.util.Optional.of(new User("user1@gmail.com", "user1", "password1", Role.ROLE_USER, true)));
        when(userRepository.findById(mockUserDto2.id())).thenReturn(java.util.Optional.of(new User("user2@gmail.com", "user2", "password2", Role.ROLE_USER, true)));
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
        when(userRepository.findById(mockUserDto1.id())).thenReturn(java.util.Optional.of(new User("user1@gmail.com", "user1", "password1", Role.ROLE_USER, true)));
        when(userRepository.findById(mockUserDto2.id())).thenReturn(java.util.Optional.of(new User("user2@gmail.com", "user2", "password2", Role.ROLE_USER, true)));
        when(friendshipRepository.findByUserAndFriend(any(User.class), any(User.class))).thenReturn(null);

        // Act & Assert
        assertThrows(FriendshipNotFoundException.class, () -> friendshipService.updateFriendshipStatus(mockUserDto2, mockUserDto1, FriendshipStatus.ACCEPTED));
        verify(friendshipRepository, never()).save(any(Friendship.class));
    }

    @Test
    void getFriendsByStatusSuccessfully() throws UserNotFoundException {
        // Arrange
        when(userRepository.findById(mockUserDto1.id())).thenReturn(java.util.Optional.of(new User("user1@gmail.com", "user1", "password1", Role.ROLE_USER, true)));
        List<FriendshipDto> friendships = List.of(mockFriendshipDto);
        when(friendshipRepository.findFriendshipsByUserAndStatus(any(User.class), eq(FriendshipStatus.PENDING))).thenReturn(friendships);

        // Act
        List<FriendshipDto> result = friendshipService.getFriendsByStatus(mockUserDto1, FriendshipStatus.PENDING);

        // Assert
        assertEquals(friendships, result);
        verify(friendshipRepository, times(1)).findFriendshipsByUserAndStatus(any(User.class), eq(FriendshipStatus.PENDING));
    }


    @Test
    void deleteFriendSuccessfully() throws FriendshipNotFoundException, UserNotFoundException {
        // Arrange
        when(userRepository.findById(mockUserDto1.id())).thenReturn(java.util.Optional.of(new User("user1@gmail.com", "user1", "password1", Role.ROLE_USER, true)));
        when(userRepository.findById(mockUserDto2.id())).thenReturn(java.util.Optional.of(new User("user2@gmail.com", "user2", "password2", Role.ROLE_USER, true)));
        when(friendshipRepository.findByUserAndFriend(any(User.class), any(User.class))).thenReturn(mockFriendship);

        // Act
        friendshipService.deleteFriend(mockUserDto1, mockUserDto2);

        // Assert
        verify(friendshipRepository, times(1)).delete(any(Friendship.class));
    }

    @Test
    void deleteFriendNotFound() {
        // Arrange
        when(userRepository.findById(mockUserDto1.id())).thenReturn(java.util.Optional.of(new User("user1@gmail.com", "user1", "password1", Role.ROLE_USER, true)));
        when(userRepository.findById(mockUserDto2.id())).thenReturn(java.util.Optional.of(new User("user2@gmail.com", "user2", "password2", Role.ROLE_USER, true)));
        when(friendshipRepository.findByUserAndFriend(any(User.class), any(User.class))).thenReturn(null);

        // Act & Assert
        assertThrows(FriendshipNotFoundException.class, () -> friendshipService.deleteFriend(mockUserDto1, mockUserDto2));
        verify(friendshipRepository, never()).delete(any(Friendship.class));
    }

    @Test
    void getFriendshipSuccessfully() throws FriendshipNotFoundException, UserNotFoundException {
        // Arrange
        when(userRepository.findById(mockUserDto1.id())).thenReturn(java.util.Optional.of(new User("user1@gmail.com", "user1", "password1", Role.ROLE_USER, true)));
        when(userRepository.findById(mockUserDto2.id())).thenReturn(java.util.Optional.of(new User("user2@gmail.com", "user2", "password2", Role.ROLE_USER, true)));
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
        when(userRepository.findById(mockUserDto1.id())).thenReturn(java.util.Optional.of(new User("user1@gmail.com", "user1", "password1", Role.ROLE_USER, true)));
        when(userRepository.findById(mockUserDto2.id())).thenReturn(java.util.Optional.of(new User("user2@gmail.com", "user2", "password2", Role.ROLE_USER, true)));
        when(friendshipRepository.findByUserAndFriend(any(User.class), any(User.class))).thenReturn(null);

        // Act & Assert
        assertThrows(FriendshipNotFoundException.class, () -> friendshipService.getFriendship(mockUserDto1, mockUserDto2));
        verify(friendshipRepository, times(1)).findByUserAndFriend(any(User.class), any(User.class));
    }
}
