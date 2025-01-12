package movie_master.api.service;

import movie_master.api.exception.TopicNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.model.Comment;
import movie_master.api.model.Topic;
import movie_master.api.model.User;
import movie_master.api.repository.CommentRepository;
import movie_master.api.repository.TopicRepository;
import movie_master.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultCommentService implements CommentService {

    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public DefaultCommentService(CommentRepository commentRepository, TopicRepository topicRepository,
                                 UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Comment> getCommentsForTopic(Long topicId) throws TopicNotFoundException {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));
        return commentRepository.findAllByTopic(topic);
    }

    @Override
    public Comment createComment(String content, Long topicId, Long userId) throws UserNotFoundException, TopicNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));
        Comment comment = new Comment(content, topic, user);
        return commentRepository.save(comment);
    }
}