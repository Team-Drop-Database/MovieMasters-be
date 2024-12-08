package movie_master.api.service;

import movie_master.api.model.Friendship;
import movie_master.api.model.User;
import movie_master.api.model.friendship.FriendshipStatus;
import movie_master.api.repository.FriendshipRepository;
import movie_master.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    public Friendship addFriend(User user, User friend) {
        if (friendshipRepository.existsByUserAndFriend(user, friend)) {
            throw new IllegalArgumentException("Friendship already exists.");
        }
        Friendship friendship = new Friendship(user, friend, FriendshipStatus.PENDING);
        return friendshipRepository.save(friendship);
    }

    public Friendship updateFriendshipStatus(User user, User friend, FriendshipStatus status) {
        Friendship friendship = friendshipRepository.findByUserAndFriend(user, friend);
        if (friendship == null) {
            throw new IllegalArgumentException("Friendship does not exist.");
        }
        friendship.setStatus(status);
        return friendshipRepository.save(friendship);
    }

    public List<Friendship> getFriendsByStatus(User user, FriendshipStatus status) {
        return friendshipRepository.findByUserAndStatus(user, status);
    }

    public void deleteFriend(User user, User friend) {
        Friendship friendship = friendshipRepository.findByUserAndFriend(user, friend);
        if (friendship != null) {
            friendshipRepository.delete(friendship);
        }
    }
    public Friendship addFriendByUsername(User user, String friendUsername) {
        User friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + friendUsername));

        if (friendshipRepository.existsByUserAndFriend(user, friend)) {
            throw new IllegalArgumentException("Friendship already exists.");
        }
        Friendship friendship = new Friendship(user, friend, FriendshipStatus.PENDING);
        return friendshipRepository.save(friendship);
    }
}
