package movie_master.api.service;

import movie_master.api.exception.TopicNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.model.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsForTopic(Long topicId) throws TopicNotFoundException;

    Comment createComment(String content, Long topicId, Long userId) throws UserNotFoundException, TopicNotFoundException;
}
