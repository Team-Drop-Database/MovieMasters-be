package movie_master.api.service;

import movie_master.api.dto.FriendshipDto;
import movie_master.api.dto.UserDto;
import movie_master.api.exception.FriendshipNotFoundException;
import movie_master.api.exception.UserNotFoundException;
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

    private User toUser(UserDto userDto) throws UserNotFoundException {
        return userRepository.findById(userDto.id())
                .orElseThrow(() -> new UserNotFoundException(userDto.id()));
    }

    public Friendship addFriend(UserDto userDto, UserDto friendDto) throws FriendshipNotFoundException, UserNotFoundException {
        User user = toUser(userDto);
        User friend = toUser(friendDto);

        if (friendshipRepository.existsByUserAndFriend(user, friend)) {
            throw new FriendshipNotFoundException(user.getUserId(), friend.getUserId());
        }
        Friendship friendship = new Friendship(user, friend, FriendshipStatus.PENDING);
        return friendshipRepository.save(friendship);
    }

    public Friendship updateFriendshipStatus(UserDto userDto, UserDto friendDto, FriendshipStatus status) throws FriendshipNotFoundException, UserNotFoundException {
        Friendship friendship = getFriendship(friendDto, userDto);

        friendship.setStatus(status);
        return friendshipRepository.save(friendship);
    }

    public List<FriendshipDto> getFriendsByStatus(UserDto userDto, FriendshipStatus status) throws UserNotFoundException {
        User user = toUser(userDto);

        return friendshipRepository.findFriendshipsByUserAndStatus(user, status);
    }

    public void deleteFriend(UserDto userDto, UserDto friendDto) throws FriendshipNotFoundException, UserNotFoundException {
        User user = toUser(userDto);
        User friend = toUser(friendDto);

        Friendship friendship = findFriendshipInBothDirections(user, friend);
        friendshipRepository.delete(friendship);

    }

    public Friendship getFriendship(UserDto userDto, UserDto friendDto) throws FriendshipNotFoundException, UserNotFoundException {
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
