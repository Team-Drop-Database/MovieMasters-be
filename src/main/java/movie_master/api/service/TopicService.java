package movie_master.api.service;

import movie_master.api.exception.UserNotFoundException;
import movie_master.api.model.Topic;

import java.util.List;

public interface TopicService {
    List<Topic> getAllTopics();

    Topic createTopic(String title, String description, Long userId) throws UserNotFoundException;
}