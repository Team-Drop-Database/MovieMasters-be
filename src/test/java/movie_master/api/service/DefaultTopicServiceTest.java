package movie_master.api.service;

import movie_master.api.dto.Forum.TopicDto;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.mapper.TopicDtoMapper;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultTopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TopicDtoMapper topicDtoMapper;

    @InjectMocks
    private DefaultTopicService topicService;

    private User mockUser;
    private Topic mockTopic;
    private TopicDto mockTopicDto;

    @BeforeEach
    void setup() {
        mockUser = new User("user1@gmail.com", "user1", "password1", null, true);
        mockUser.setUserId(1L);

        mockTopic = new Topic("Test Title", "Test Description", mockUser);
        mockTopic.setTopicId(1L);

        mockTopicDto = new TopicDto(
                mockTopic.getTopicId(),
                mockTopic.getTitle(),
                mockTopic.getDescription(),
                mockUser.getUsername(),
                mockUser.getProfilePicture(),
                mockTopic.getComments().size(),
                mockTopic.getCreatedAt()
        );
    }

    @Test
    void getAllTopicsSuccessfully() {
        // Arrange
        List<Topic> mockTopics = List.of(mockTopic);
        when(topicRepository.findAll()).thenReturn(mockTopics);
        when(topicDtoMapper.toTopicDto(mockTopic)).thenReturn(mockTopicDto);

        // Act
        List<TopicDto> result = topicService.getAllTopics();

        // Assert
        assertEquals(List.of(mockTopicDto), result);
        verify(topicRepository, times(1)).findAll();
        verify(topicDtoMapper, times(1)).toTopicDto(mockTopic);
    }

    @Test
    void createTopicSuccessfully() throws UserNotFoundException {
        // Arrange
        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
        when(topicRepository.save(any(Topic.class))).thenReturn(mockTopic);
        when(topicDtoMapper.toTopicDto(mockTopic)).thenReturn(mockTopicDto);

        // Act
        TopicDto result = topicService.createTopic("Test Title", "Test Description", mockUser.getUserId());

        // Assert
        assertEquals(mockTopicDto, result);
        verify(topicRepository, times(1)).save(any(Topic.class));
        verify(topicDtoMapper, times(1)).toTopicDto(mockTopic);
    }

    @Test
    void createTopicUserNotFound() {
        // Arrange
        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                topicService.createTopic("Test Title", "Test Description", mockUser.getUserId()));
        verify(topicRepository, never()).save(any(Topic.class));
    }
}