package movie_master.api.repository;

import movie_master.api.model.Comment;
import movie_master.api.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByTopic(Topic topic);
}