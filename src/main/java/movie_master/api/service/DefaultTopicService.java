package movie_master.api.service;

import movie_master.api.exception.UserNotFoundException;
import movie_master.api.model.Topic;
import movie_master.api.model.User;
import movie_master.api.repository.TopicRepository;
import movie_master.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultTopicService implements TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public DefaultTopicService(TopicRepository topicRepository, UserRepository userRepository) {
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    @Override
    public Topic createTopic(String title, String description, Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Topic topic = new Topic(title, description, user);
        return topicRepository.save(topic);
    }
}
