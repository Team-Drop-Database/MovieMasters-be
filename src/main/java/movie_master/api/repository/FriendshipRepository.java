package movie_master.api.repository;


import movie_master.api.model.Friendship;
import movie_master.api.model.User;
import movie_master.api.model.friendship.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByUserAndStatus(User user, FriendshipStatus status);
    List<Friendship> findByFriendAndStatus(User friend, FriendshipStatus status);
    boolean existsByUserAndFriend(User user, User friend);
}