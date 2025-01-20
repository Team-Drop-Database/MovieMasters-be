package movie_master.api.exception;

public class TopicNotFoundException extends Exception {
    public TopicNotFoundException(Long topicId) {
        super("Topic with id '%d' does not exist".formatted(topicId));
    }
}