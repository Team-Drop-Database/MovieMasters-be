package movie_master.api.service;

import movie_master.api.dto.Forum.CommentDto;
import movie_master.api.exception.TopicNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.mapper.CommentDtoMapper;
import movie_master.api.model.Comment;
import movie_master.api.model.Topic;
import movie_master.api.model.User;
import movie_master.api.repository.CommentRepository;
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
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentDtoMapper commentDtoMapper;

    @InjectMocks
    private CommentService commentService;

    private User mockUser;
    private Topic mockTopic;
    private Comment mockComment;
    private CommentDto mockCommentDto;

    @BeforeEach
    void setup() {
        mockUser = new User("user1@gmail.com", "user1", "password1", null, true, false);
        mockUser.setUserId(1L);

        mockTopic = new Topic("Test Title", "Test Description", mockUser);
        mockTopic.setTopicId(1L);

        mockComment = new Comment("Test Comment", mockTopic, mockUser);
        mockComment.setCommentId(1L);
        mockCommentDto = new CommentDto(
                mockComment.getCommentId(),
                mockComment.getContent(),
                mockUser.getUsername(),
                mockUser.getProfilePicture(),
                mockComment.getCreatedAt()
        );
    }

    @Test
    void getCommentsForTopicSuccessfully() throws TopicNotFoundException {
        // Arrange
        List<Comment> mockComments = List.of(mockComment);
        when(topicRepository.findById(mockTopic.getTopicId())).thenReturn(Optional.of(mockTopic));
        when(commentRepository.findAllByTopic(mockTopic)).thenReturn(mockComments);
        when(commentDtoMapper.toCommentDto(mockComment)).thenReturn(mockCommentDto);

        // Act
        List<CommentDto> result = commentService.getCommentsForTopic(mockTopic.getTopicId());

        // Assert
        assertEquals(List.of(mockCommentDto), result);
        verify(commentRepository, times(1)).findAllByTopic(mockTopic);
        verify(commentDtoMapper, times(1)).toCommentDto(mockComment);
    }

    @Test
    void getCommentsForTopicTopicNotFound() {
        // Arrange
        when(topicRepository.findById(mockTopic.getTopicId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TopicNotFoundException.class, () ->
                commentService.getCommentsForTopic(mockTopic.getTopicId()));
        verify(commentRepository, never()).findAllByTopic(any(Topic.class));
    }

    @Test
    void createCommentSuccessfully() throws UserNotFoundException, TopicNotFoundException {
        // Arrange
        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
        when(topicRepository.findById(mockTopic.getTopicId())).thenReturn(Optional.of(mockTopic));
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);
        when(commentDtoMapper.toCommentDto(mockComment)).thenReturn(mockCommentDto);

        // Act
        CommentDto result = commentService.createComment("Test Comment", mockTopic.getTopicId(), mockUser.getUserId());

        // Assert
        assertEquals(mockCommentDto, result);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(commentDtoMapper, times(1)).toCommentDto(mockComment);
    }

    @Test
    void createCommentUserNotFound() {
        // Arrange
        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                commentService.createComment("Test Comment", mockTopic.getTopicId(), mockUser.getUserId()));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createCommentTopicNotFound() {
        // Arrange
        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
        when(topicRepository.findById(mockTopic.getTopicId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TopicNotFoundException.class, () ->
                commentService.createComment("Test Comment", mockTopic.getTopicId(), mockUser.getUserId()));
        verify(commentRepository, never()).save(any(Comment.class));
    }
}