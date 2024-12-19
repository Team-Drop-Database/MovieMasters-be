package movie_master.api.repository;

import movie_master.api.dto.FriendshipDto;
import movie_master.api.model.Friendship;
import movie_master.api.model.User;
import movie_master.api.model.friendship.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT new movie_master.api.dto.FriendshipDto(" +
            "f.id, " +
            "CASE WHEN f.user = :user THEN f.friend.username ELSE f.user.username END, " +
            "f.status, f.friendshipDate) " +
            "FROM Friendship f " +
            "WHERE (f.user = :user OR f.friend = :user) AND f.status = :status")
    List<FriendshipDto> findFriendshipsByUserAndStatus(@Param("user") User user,
                                                       @Param("status") FriendshipStatus status);
    boolean existsByUserAndFriend(User user, User friend);
    Friendship findByUserAndFriend(User user, User friend);
}