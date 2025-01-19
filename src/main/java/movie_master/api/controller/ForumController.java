package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import movie_master.api.dto.Forum.CommentDto;
import movie_master.api.dto.Forum.TopicDto;
import movie_master.api.exception.TopicNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.request.TopicRequest;
import movie_master.api.jwt.JwtUtil;
import movie_master.api.service.CommentService;
import movie_master.api.service.TopicService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/forum")
public class ForumController {

    private final TopicService topicService;
    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    public ForumController(TopicService topicService, CommentService commentService, JwtUtil jwtUtil) {
        this.topicService = topicService;
        this.commentService = commentService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/topics")
    public ResponseEntity<List<TopicDto>> getAllTopics() {
        List<TopicDto> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/topics/{topicId}")
    public ResponseEntity<Object> getTopicById(@PathVariable Long topicId) {
        try {
            TopicDto topic = topicService.getTopicById(topicId);
            return ResponseEntity.ok(topic);
        } catch (TopicNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/topics/{topicId}/comments")
    public ResponseEntity<Object> getCommentsForTopic(@PathVariable Long topicId) {
        try {
            List<CommentDto> comments = commentService.getCommentsForTopic(topicId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/topics")
    public ResponseEntity<Object> createTopic(
            @Valid @RequestBody TopicRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt,
            HttpServletRequest httpServletRequest
    ) {
        try {
            Long userId = jwtUtil.getUserId(jwt.replace("Bearer ", ""));

            TopicDto topicDto = topicService.createTopic(request.title(), request.description(), userId);
            return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI())).body(topicDto);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/topics/{topicId}/comments")
    public ResponseEntity<Object> createComment(
            @PathVariable Long topicId,
            @Valid @RequestBody String content,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt,
            HttpServletRequest httpServletRequest
    ) {
        try {
            Long userId = jwtUtil.getUserId(jwt.replace("Bearer ", ""));

            CommentDto commentDto = commentService.createComment(content, topicId, userId);
            return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI())).body(commentDto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}