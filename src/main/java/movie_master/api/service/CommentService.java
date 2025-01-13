package movie_master.api.service;

import movie_master.api.dto.Forum.CommentDto;
import movie_master.api.exception.TopicNotFoundException;
import movie_master.api.exception.UserNotFoundException;

import java.util.List;

public interface CommentService {
    List<CommentDto> getCommentsForTopic(Long topicId) throws TopicNotFoundException;

    CommentDto createComment(String content, Long topicId, Long userId) throws UserNotFoundException, TopicNotFoundException;
}
