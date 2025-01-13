package movie_master.api.dto.Forum;

import movie_master.api.model.Topic;

import java.time.LocalDateTime;

/**
 * Data transfer object for comments
 * @param id
 * @param content
 * @param username
 * @param profilePicture
 * @param topic
 * @param creationDate
 */
public record CommentDto(Long id, String content, String username, String profilePicture, Topic topic, LocalDateTime creationDate) {}
