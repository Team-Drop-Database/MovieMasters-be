package movie_master.api.service;

import movie_master.api.dto.UserDto;
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

    private User toUser(UserDto userDto) {
        return new User(userDto);
    }

    public FriendshipService(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    public Friendship addFriend(UserDto userDto, UserDto friendDto) throws FriendshipNotFoundException {
        User user = toUser(userDto);
        User friend = toUser(friendDto);

        if (friendshipRepository.existsByUserAndFriend(user, friend)) {
            throw new FriendshipNotFoundException(user.getUserId(), friend.getUserId());
        }
        Friendship friendship = new Friendship(user, friend, FriendshipStatus.PENDING);
        return friendshipRepository.save(friendship);
    }

    public Friendship updateFriendshipStatus(UserDto userDto, UserDto friendDto, FriendshipStatus status) throws FriendshipNotFoundException {
        Friendship friendship = getFriendship(userDto, friendDto);
        if (friendship == null) {
            throw new FriendshipNotFoundException(friendDto.id(), friendDto.id());
        }
        friendship.setStatus(status);
        return friendshipRepository.save(friendship);
    }

    public List<Friendship> getFriendsByStatus(UserDto userDto, FriendshipStatus status) {
        User user = toUser(userDto);
        return friendshipRepository.findByUserAndStatus(user, status);
    }

    public void deleteFriend(UserDto userDto, UserDto friendDto) throws FriendshipNotFoundException{
        User user = toUser(userDto);
        User friend = toUser(friendDto);

        Friendship friendship = findFriendshipInBothDirections(user, friend);
        friendshipRepository.delete(friendship);

    }

    public Friendship getFriendship(UserDto userDto, UserDto friendDto) throws FriendshipNotFoundException {
        User user = toUser(userDto);
        User friend = toUser(friendDto);

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
