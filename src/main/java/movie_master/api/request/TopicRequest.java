package movie_master.api.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Represents the body of an HTTP request for managing topics.
 *
 * @param title         - the title of the topic
 * @param description   - the description of the topic
 */
public record TopicRequest(
        @NotBlank(message = "Title is required.") String title,
        @NotBlank(message = "Description is required.") String description
) {}