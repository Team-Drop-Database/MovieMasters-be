package movie_master.api.service;

import movie_master.api.model.Friendship;
import movie_master.api.model.friendship.FriendshipStatus;
import movie_master.api.repository.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    public Friendship addFriend(Long userId, Long friendId) {
        FriendshipId friendshipId = new FriendshipId(userId.intValue(), friendId.intValue());
        Friendship friendship = new Friendship(friendshipId, FriendshipStatus.PENDING);
        return friendshipRepository.save(friendship);
    }

    // Get list of accepted friends for a user
    public List<Friendship> getFriends(Long userId) {
        return friendshipRepository.findByFriendshipIdUserIdAndStatus(userId.intValue(), FriendshipStatus.ACCEPTED);
    }

    // Remove a friend by deleting the friendship record
    public void removeFriend(Long userId, Long friendId) {
        FriendshipId friendshipId = new FriendshipId(userId.intValue(), friendId.intValue());
        friendshipRepository.deleteById(friendshipId);
    }

    // Update the friendship status (e.g., accept or reject a request)
    public Friendship updateFriendshipStatus(Long userId, Long friendId, FriendshipStatus status) {
        FriendshipId friendshipId = new FriendshipId(userId.intValue(), friendId.intValue());
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friendship not found"));
        friendship.setStatus(status);
        return friendshipRepository.save(friendship);
    }
}
