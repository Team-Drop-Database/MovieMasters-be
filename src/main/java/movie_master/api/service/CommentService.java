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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final CommentDtoMapper commentDtoMapper;

    public CommentService(CommentRepository commentRepository, TopicRepository topicRepository,
                          UserRepository userRepository, CommentDtoMapper commentDtoMapper) {
        this.commentRepository = commentRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
        this.commentDtoMapper = commentDtoMapper;
    }

    public List<CommentDto> getCommentsForTopic(Long topicId) throws TopicNotFoundException {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new TopicNotFoundException(topicId));
        List<Comment> comments = commentRepository.findAllByTopic(topic);

        return comments.stream()
                .map(commentDtoMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public CommentDto createComment(String content, Long topicId, Long userId)
            throws UserNotFoundException, TopicNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new TopicNotFoundException(topicId));
        Comment comment = new Comment(content, topic, user);
        return commentDtoMapper.toCommentDto(commentRepository.save(comment));
    }
}