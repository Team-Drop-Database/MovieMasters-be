package movie_master.api.repository;

import jakarta.transaction.Transactional;
import movie_master.api.model.Friendship;
import movie_master.api.model.friendship.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    /**
     * Retrieves friendships for a given user with the specified status and maps them to FriendshipDto objects.
     *
     * @param userId The ID of the user whose friendships are being queried.
     * @param status The status of the friendships to filter by (e.g., PENDING, ACCEPTED).
     * @return A list of FriendshipDto objects containing relevant friendship details.
     */
    @Query("SELECT f " +
            "FROM Friendship f " +
            "WHERE (f.user.userId = :userId OR f.friend.userId = :userId) AND f.status = :status")
    List<Friendship> findFriendshipsByUserAndStatus(@Param("userId") Long userId,
                                                    @Param("status") FriendshipStatus status);

    /**
     * Checks if a friendship already exists between two users identified by their IDs.
     *
     * @param userId  The ID of the user who is part of the friendship.
     * @param friendId The ID of the friend who is part of the friendship.
     * @return true if a friendship exists between the two users, otherwise false.
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM Friendship f " +
            "WHERE (f.user.userId = :userId AND f.friend.userId = :friendId)")
    boolean existsByUserIdAndFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId);

    /**
     * Retrieves a friendship by the two users' IDs.
     *
     * @param userId  The ID of the user who is part of the friendship.
     * @param friendId The ID of the friend who is part of the friendship.
     * @return The Friendship object if found, otherwise null.
     */
    @Query("SELECT f FROM Friendship f WHERE (f.user.userId = :userId AND f.friend.userId = :friendId)")
    Friendship findByUserIdAndFriendId(Long userId, Long friendId);

    /**
     * Deletes all friendships where the given user is involved.
     *
     * @param userId The ID of the user you want to alienate.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Friendship f WHERE f.user.userId = :userId OR f.friend.userId = :userId")
    void deleteFriendshipByUser(Long userId);
}