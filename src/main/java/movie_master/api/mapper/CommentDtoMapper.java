package movie_master.api.mapper;

import movie_master.api.dto.Forum.CommentDto;
import movie_master.api.model.Comment;
import org.springframework.stereotype.Service;

/**
 * Class that contains a function that maps a comment object to a comment dto object
 * A data transfer object is being used to control which data of a model
 * will be exposed to the client.
 */
@Service
public class CommentDtoMapper {

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getCommentId(),
                comment.getContent(),
                comment.getUser().getUsername(),
                comment.getUser().getProfilePicture(),
                comment.getTopic(),
                comment.getCreatedAt()
        );
    }
}