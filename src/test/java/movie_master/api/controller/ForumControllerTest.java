package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import movie_master.api.exception.TopicNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.model.Comment;
import movie_master.api.model.Topic;
import movie_master.api.model.User;
import movie_master.api.model.role.Role;
import movie_master.api.service.TopicService;
import movie_master.api.service.CommentService;
import movie_master.api.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ForumControllerTest {

    @Mock private TopicService topicService;
    @Mock private CommentService commentService;
    @Mock private JwtUtil jwtUtil;
    @InjectMocks private ForumController forumController;

    private String jwtTokenUser1;
    private User mockUser1;
    private Topic mockTopic;
    private Comment mockComment;

    @BeforeEach
    void setup() {
        jwtTokenUser1 = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwidXNlcklkIjoxLCJzdWIiOiJ1c2VyMSIsImlhdCI6MTczMzc2Nzc4MCwiZXhwIjoxNzQxNTQzNzgwfQ.0yWfkHVkb9vzQi4Raq-VxNAsKBFuZWBRqC3bR0FgZWI";
        String jwtTokenUser2 = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwidXNlcklkIjoyLCJzdWIiOiJ1c2VyMiIsImlhdCI6MTczMzc2Nzc4MCwiZXhwIjoxNzQxNTQzNzgwfQ.xWFksILDJbDk8E7FXi1JEBuCkS43-G3OQgRhY2lQKkg";

        mockUser1 = new User("user1@gmail.com", "user1", "password1", Role.ROLE_USER, true);
        mockUser1.setUserId(1L);
        User mockUser2 = new User("user2@gmail.com", "user2", "password2", Role.ROLE_USER, true);
        mockUser2.setUserId(2L);

        mockTopic = new Topic("Sample Topic", "This is a sample topic description", mockUser1);
        mockTopic.setTopicId(1L);

        mockComment = new Comment("This is a comment", mockTopic, mockUser1);
        mockComment.setCommentId(1L);
    }

    @Test
    void createTopicSuccessfully() throws UserNotFoundException {
        // Arrange
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        when(jwtUtil.getUserId(jwtTokenUser1.replace("Bearer ", ""))).thenReturn(mockUser1.getUserId());
        when(topicService.createTopic(mockTopic.getTitle(), mockTopic.getDescription(), mockUser1.getUserId())).thenReturn(mockTopic);
        when(mockRequest.getRequestURI()).thenReturn("/forum/topics");

        // Act
        ResponseEntity<Object> response = forumController.createTopic(mockTopic, jwtTokenUser1, mockRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockTopic, response.getBody());
    }

    @Test
    void createCommentSuccessfully() throws UserNotFoundException, TopicNotFoundException {
        // Arrange
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        when(jwtUtil.getUserId(jwtTokenUser1.replace("Bearer ", ""))).thenReturn(mockUser1.getUserId());
        when(commentService.createComment(mockComment.getContent(), mockTopic.getTopicId(), mockUser1.getUserId())).thenReturn(mockComment);
        when(mockRequest.getRequestURI()).thenReturn("/forum/topics/1/comments");

        // Act
        ResponseEntity<Object> response = forumController.createComment(mockTopic.getTopicId(), mockComment, jwtTokenUser1, mockRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockComment, response.getBody());
    }

    @Test
    void getAllTopicsSuccessfully() {
        // Arrange
        List<Topic> topics = List.of(mockTopic);
        when(topicService.getAllTopics()).thenReturn(topics);

        // Act
        ResponseEntity<List<Topic>> response = forumController.getAllTopics();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(topics, response.getBody());
    }

    @Test
    void getCommentsForTopicSuccessfully() throws TopicNotFoundException {
        // Arrange
        List<Comment> comments = List.of(mockComment);
        when(commentService.getCommentsForTopic(mockTopic.getTopicId())).thenReturn(comments);

        // Act
        ResponseEntity<Object> response = forumController.getCommentsForTopic(mockTopic.getTopicId());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comments, response.getBody());
    }

    @Test
    void getCommentsForTopicFailure() throws TopicNotFoundException {
        // Arrange
        when(commentService.getCommentsForTopic(mockTopic.getTopicId())).thenThrow(new RuntimeException("Failed to fetch comments"));

        // Act
        ResponseEntity<Object> response = forumController.getCommentsForTopic(mockTopic.getTopicId());

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to fetch comments", response.getBody());
    }
}