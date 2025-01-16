package movie_master.api.dto.Forum;

import java.time.LocalDateTime;

/**
 * Data transfer object for comments
 * @param id
 * @param content
 * @param username
 * @param profilePicture
 * @param creationDate
 */
public record CommentDto(Long id, String content, String username, String profilePicture, LocalDateTime creationDate) {}
