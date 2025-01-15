package movie_master.api.service;

import movie_master.api.dto.Forum.TopicDto;
import movie_master.api.exception.TopicNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.mapper.TopicDtoMapper;
import movie_master.api.model.Topic;
import movie_master.api.model.User;
import movie_master.api.repository.TopicRepository;
import movie_master.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultTopicService implements TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final TopicDtoMapper topicDtoMapper;

    public DefaultTopicService(TopicRepository topicRepository, UserRepository userRepository, TopicDtoMapper topicDtoMapper) {
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
        this.topicDtoMapper = topicDtoMapper;
    }

    @Override
    public List<TopicDto> getAllTopics() {
        List<Topic> topics = topicRepository.findAll();
        return topics.stream()
                .map(this.topicDtoMapper::toTopicDto)
                .collect(Collectors.toList());
    }

    @Override
    public TopicDto getTopicById(Long topicId) throws TopicNotFoundException {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));
        return this.topicDtoMapper.toTopicDto(topic);
    }

    @Override
    public TopicDto createTopic(String title, String description, Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Topic topic = new Topic(title, description, user);
        return this.topicDtoMapper.toTopicDto(topicRepository.save(topic));
    }
}
