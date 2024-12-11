package movie_master.api.service;

import movie_master.api.exception.FriendshipNotFoundException;
import movie_master.api.model.Friendship;
import movie_master.api.model.User;
import movie_master.api.model.friendship.FriendshipStatus;
import movie_master.api.repository.FriendshipRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;

    public FriendshipService(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    public Friendship addFriend(User user, User friend) throws FriendshipNotFoundException {
        if (friendshipRepository.existsByUserAndFriend(user, friend)) {
            throw new FriendshipNotFoundException(user.getUserId(), friend.getUserId());
        }
        Friendship friendship = new Friendship(user, friend, FriendshipStatus.PENDING);
        return friendshipRepository.save(friendship);
    }

    public Friendship updateFriendshipStatus(User friend, User user, FriendshipStatus status) throws FriendshipNotFoundException {
        Friendship friendship = getFriendship(friend, user);
        if (friendship == null) {
            throw new FriendshipNotFoundException(user.getUserId(), friend.getUserId());
        }
        friendship.setStatus(status);
        return friendshipRepository.save(friendship);
    }

    public List<Friendship> getFriendsByStatus(User user, FriendshipStatus status) {
        return friendshipRepository.findByUserAndStatus(user, status);
    }

    public void deleteFriend(User user, User friend) throws FriendshipNotFoundException{
        Friendship friendship = findFriendshipInBothDirections(user, friend);
        friendshipRepository.delete(friendship);

    }

    public Friendship getFriendship(User user, User friend) throws FriendshipNotFoundException {
        Friendship friendship = friendshipRepository.findByUserAndFriend(user, friend);
        if (friendship == null) {
            throw new FriendshipNotFoundException(user.getUserId(), friend.getUserId());
        }
        return friendship;
    }

    //Helper function for getting a friendship in both directions
    private Friendship findFriendshipInBothDirections(User user, User friend) throws FriendshipNotFoundException {
        Friendship friendship = friendshipRepository.findByUserAndFriend(user, friend);
        if (friendship == null) {
            friendship = friendshipRepository.findByUserAndFriend(friend, user);
        }
        if (friendship == null) {
            throw new FriendshipNotFoundException(user.getUserId(), friend.getUserId());
        }
        return friendship;
    }
}
