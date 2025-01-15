package movie_master.api.service;

import movie_master.api.dto.Forum.TopicDto;
import movie_master.api.exception.TopicNotFoundException;
import movie_master.api.exception.UserNotFoundException;

import java.util.List;

public interface TopicService {
    List<TopicDto> getAllTopics();

    TopicDto getTopicById(Long topicId) throws TopicNotFoundException;

    TopicDto createTopic(String title, String description, Long userId) throws UserNotFoundException;
}