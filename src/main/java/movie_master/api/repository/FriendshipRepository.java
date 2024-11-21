package movie_master.api.repository;


import movie_master.api.model.Friendship;
import movie_master.api.model.helper.FriendshipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipId> {
    List<Friendship> findByFriendshipId_UserId(Integer userId);

    List<Friendship> findByFriendshipId_FriendId(Integer friendId);

    List<Friendship> findByStatus(String status);
}