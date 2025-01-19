package movie_master.api.dto.Forum;

import java.time.LocalDateTime;

/**
 * Data transfer object for topics
 * @param id
 * @param title
 * @param description
 * @param createdByUsername
 * @param createdByProfilePicture
 * @param amountComments
 * @param creationDate
 */
public record TopicDto(Long id, String title, String description, String createdByUsername,
                       String createdByProfilePicture, int amountComments, LocalDateTime creationDate) {}
