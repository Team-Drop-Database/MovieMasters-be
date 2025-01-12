package movie_master.api.service;

import movie_master.api.exception.UserNotFoundException;
import movie_master.api.model.Topic;
import movie_master.api.model.User;
import movie_master.api.repository.TopicRepository;
import movie_master.api.repository.UserRepository;
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
class DefaultTopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DefaultTopicService topicService;

    private User mockUser;
    private Topic mockTopic;

    @BeforeEach
    void setup() {
        mockUser = new User("user1@gmail.com", "user1", "password1", null, true);
        mockUser.setUserId(1L);
        mockTopic = new Topic("Test Title", "Test Description", mockUser);
    }

    @Test
    void getAllTopicsSuccessfully() {
        // Arrange
        List<Topic> mockTopics = List.of(mockTopic);
        when(topicRepository.findAll()).thenReturn(mockTopics);

        // Act
        List<Topic> result = topicService.getAllTopics();

        // Assert
        assertEquals(mockTopics, result);
        verify(topicRepository, times(1)).findAll();
    }

    @Test
    void createTopicSuccessfully() throws UserNotFoundException {
        // Arrange
        when(userRepository.findById(mockUser.getUserId())).thenReturn(java.util.Optional.of(mockUser));
        when(topicRepository.save(any(Topic.class))).thenReturn(mockTopic);

        // Act
        Topic result = topicService.createTopic("Test Title", "Test Description", mockUser.getUserId());

        // Assert
        assertEquals(mockTopic, result);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void createTopicUserNotFound() {
        // Arrange
        when(userRepository.findById(mockUser.getUserId())).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> topicService.createTopic("Test Title", "Test Description", mockUser.getUserId()));
        verify(topicRepository, never()).save(any(Topic.class));
    }
}