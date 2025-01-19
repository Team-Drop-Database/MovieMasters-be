package movie_master.api.mapper;

import movie_master.api.dto.Forum.TopicDto;
import movie_master.api.model.Topic;
import org.springframework.stereotype.Service;

/**
 * Class that contains a function that maps a topic object to a topic dto object
 * A data transfer object is being used to control which data of a model
 * will be exposed to the client.
 */
@Service
public class TopicDtoMapper {

    public TopicDto toTopicDto(Topic topic) {
        return new TopicDto(
                topic.getTopicId(),
                topic.getTitle(),
                topic.getDescription(),
                topic.getUser().getUsername(),
                topic.getUser().getProfilePicture(),
                topic.getComments().size(),
                topic.getCreatedAt()
        );
    }
}