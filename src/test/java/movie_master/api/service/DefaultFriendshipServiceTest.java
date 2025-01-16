package movie_master.api.service;

import movie_master.api.dto.FriendshipDto;
import movie_master.api.exception.FriendshipAlreadyExistsException;
import movie_master.api.exception.FriendshipNotFoundException;
import movie_master.api.exception.UserCannotFriendThemself;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.mapper.FriendshipDtoMapper;
import movie_master.api.model.Friendship;
import movie_master.api.model.User;
import movie_master.api.model.friendship.FriendshipStatus;
import movie_master.api.repository.FriendshipRepository;
import movie_master.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultFriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendshipDtoMapper friendshipDtoMapper;

    @InjectMocks
    private DefaultFriendshipService defaultFriendshipService;

    private User mockUser1;
    private User mockUser2;
    private Friendship mockFriendship;

    @BeforeEach
    void setup() {
        // Arrange: Create test users and friendships
        mockUser1 = new User("user1@gmail.com", "user1", "password1", null, true);
        mockUser1.setUserId(1L);

        mockUser2 = new User("user2@gmail.com", "user2", "password2", null, true);
        mockUser2.setUserId(2L);

        mockFriendship = new Friendship(mockUser1, mockUser2, FriendshipStatus.PENDING);
    }

    @Test
    void addFriendSuccessfully() throws FriendshipAlreadyExistsException, UserNotFoundException, UserCannotFriendThemself {
        // Arrange
        FriendshipDto friendshipDto = friendshipDtoMapper.toFriendshipDto(mockFriendship, mockUser2.getUserId());

        when(userRepository.findById(mockUser1.getUserId())).thenReturn(java.util.Optional.of(mockUser1));
        when(userRepository.findByUsername(mockUser2.getUsername())).thenReturn(java.util.Optional.of(mockUser2));
        when(friendshipRepository.existsByUserIdAndFriendId(mockUser1.getUserId(), mockUser2.getUserId())).thenReturn(false);
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(mockFriendship);
        when(friendshipDtoMapper.toFriendshipDto(any(Friendship.class), eq(mockUser1.getUserId()))).thenReturn(friendshipDto);

        // Act
        FriendshipDto result = defaultFriendshipService.addFriend(mockUser1.getUserId(), mockUser2.getUsername());

        // Assert
        assertEquals(friendshipDto, result);
        verify(friendshipRepository, times(1)).save(any(Friendship.class));
        verify(friendshipDtoMapper, times(1)).toFriendshipDto(any(Friendship.class), eq(mockUser1.getUserId()));
    }

    @Test
    void addFriendAlreadyExists()  {
        // Arrange
        when(userRepository.findById(mockUser1.getUserId())).thenReturn(java.util.Optional.of(mockUser1));
        when(userRepository.findByUsername(mockUser2.getUsername())).thenReturn(java.util.Optional.of(mockUser2));
        when(friendshipRepository.existsByUserIdAndFriendId(mockUser1.getUserId(), mockUser2.getUserId())).thenReturn(true);

        // Act & Assert
        assertThrows(FriendshipAlreadyExistsException.class, () -> defaultFriendshipService.addFriend(mockUser1.getUserId(), mockUser2.getUsername()));
        verify(friendshipRepository, never()).save(any(Friendship.class));
    }

    @Test
    void updateFriendshipStatusSuccessfully() throws FriendshipNotFoundException, UserNotFoundException {
        // Arrange
        FriendshipDto updatedFriendshipDto = new FriendshipDto(mockUser1.getUserId(), mockUser1.getUsername(), mockUser1.getUserId(), mockUser2.getUsername(), mockUser2.getUserId(), mockUser2.getProfilePicture(), FriendshipStatus.ACCEPTED, LocalDateTime.now());

        //FriendshipDto updatedFriendshipDto = new FriendshipDto(mockUser1.getUserId(), mockUser1.getUsername(), mockUser1.getUserId(), mockUser2.getUsername(), mockUser2.getUserId(), null, FriendshipStatus.ACCEPTED, LocalDateTime.now());
        when(userRepository.findByUsername(mockUser2.getUsername())).thenReturn(java.util.Optional.of(mockUser2));
        when(userRepository.findById(mockUser1.getUserId())).thenReturn(java.util.Optional.of(mockUser1));
        when(friendshipRepository.findByUserIdAndFriendId(mockUser2.getUserId(), mockUser1.getUserId())).thenReturn(mockFriendship);
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(mockFriendship);
        lenient().when(friendshipDtoMapper.toFriendshipDto(eq(mockFriendship), eq(mockUser1.getUserId())))
                .thenReturn(updatedFriendshipDto);

        // Act
        FriendshipDto result = defaultFriendshipService.updateFriendshipStatus(mockUser2.getUsername(), mockUser1.getUserId(), FriendshipStatus.ACCEPTED);

        // Assert
        assertNotNull(result);
        assertEquals(FriendshipStatus.ACCEPTED, result.status());
        assertEquals(updatedFriendshipDto, result);
        verify(friendshipRepository, times(1)).save(any(Friendship.class));
        verify(friendshipDtoMapper, times(1)).toFriendshipDto(eq(mockFriendship), eq(mockUser1.getUserId()));
    }



    @Test
    void getFriendsByStatusSuccessfully() {
        // Arrange
        List<Friendship> mockFriendships = new ArrayList<>();
        mockFriendships.add(mockFriendship);

        FriendshipDto expectedFriendshipDto = new FriendshipDto(
                mockFriendship.getId(),
                null,
                mockFriendship.getUser().getUserId(),
                mockFriendship.getFriend().getUsername(),
                mockFriendship.getFriend().getUserId(),
                mockFriendship.getFriend().getProfilePicture(),
                mockFriendship.getStatus(),
                mockFriendship.getFriendshipDate()
        );

        List<FriendshipDto> expectedFriendshipDtos = new ArrayList<>();
        expectedFriendshipDtos.add(expectedFriendshipDto);

        when(friendshipRepository.findFriendshipsByUserAndStatus(mockUser1.getUserId(), FriendshipStatus.PENDING))
                .thenReturn(mockFriendships);
        when(friendshipDtoMapper.toFriendshipDto(any(Friendship.class), anyLong())).thenReturn(expectedFriendshipDto);

        // Act
        List<FriendshipDto> result = defaultFriendshipService.getFriendsByStatus(mockUser1.getUserId(), FriendshipStatus.PENDING);

        // Assert
        assertEquals(expectedFriendshipDtos, result);
        verify(friendshipRepository, times(1)).findFriendshipsByUserAndStatus(mockUser1.getUserId(), FriendshipStatus.PENDING);
    }

    @Test
    void deleteFriendSuccessfully() throws FriendshipNotFoundException, UserNotFoundException {
        // Arrange
        when(userRepository.findById(mockUser1.getUserId())).thenReturn(java.util.Optional.of(mockUser1));
        when(userRepository.findByUsername(mockUser2.getUsername())).thenReturn(java.util.Optional.of(mockUser2));
        when(friendshipRepository.findByUserIdAndFriendId(mockUser1.getUserId(), mockUser2.getUserId())).thenReturn(mockFriendship);

        // Act
        defaultFriendshipService.deleteFriend(mockUser1.getUserId(), mockUser2.getUsername());

        // Assert
        verify(friendshipRepository, times(1)).delete(any(Friendship.class));
    }

    @Test
    void getFriendshipSuccessfully() throws FriendshipNotFoundException {
        // Arrange
        when(friendshipRepository.findByUserIdAndFriendId(mockUser1.getUserId(), mockUser2.getUserId())).thenReturn(mockFriendship);

        // Act
        Friendship result = defaultFriendshipService.getFriendship(mockUser1, mockUser2);

        // Assert
        assertEquals(mockFriendship, result);
        verify(friendshipRepository, times(1)).findByUserIdAndFriendId(mockUser1.getUserId(), mockUser2.getUserId());
    }

    @Test
    void getFriendshipNotFound() {
        // Arrange
        when(friendshipRepository.findByUserIdAndFriendId(mockUser1.getUserId(), mockUser2.getUserId())).thenReturn(null);

        // Act & Assert
        assertThrows(FriendshipNotFoundException.class, () -> defaultFriendshipService.getFriendship(mockUser1, mockUser2));
    }

    @Test
    void deleteFriendshipNotFound() {
        // Arrange
        when(userRepository.findById(mockUser1.getUserId())).thenReturn(java.util.Optional.of(mockUser1));
        when(userRepository.findByUsername(mockUser2.getUsername())).thenReturn(java.util.Optional.of(mockUser2));
        when(friendshipRepository.findByUserIdAndFriendId(mockUser1.getUserId(), mockUser2.getUserId())).thenReturn(null);

        // Act & Assert
        assertThrows(FriendshipNotFoundException.class, () -> defaultFriendshipService.deleteFriend(mockUser1.getUserId(), mockUser2.getUsername()));
        verify(friendshipRepository, never()).delete(any(Friendship.class));
    }
}