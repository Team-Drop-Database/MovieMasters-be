package movie_master.api.service;

import movie_master.api.dto.FriendshipDto;
import movie_master.api.exception.FriendshipAlreadyExistsException;
import movie_master.api.exception.FriendshipNotFoundException;
import movie_master.api.exception.UserCannotFriendThemself;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.mapper.FriendshipDtoMapper;
import movie_master.api.model.Friendship;
import movie_master.api.model.User;
import movie_master.api.model.friendship.FriendshipStatus;
import movie_master.api.repository.FriendshipRepository;
import movie_master.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultFriendshipService implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final FriendshipDtoMapper friendshipDtoMapper;

    public DefaultFriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository, FriendshipDtoMapper friendshipDtoMapper) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.friendshipDtoMapper = friendshipDtoMapper;
    }

    public FriendshipDto addFriend(Long userId, String username) throws FriendshipAlreadyExistsException, UserNotFoundException, UserCannotFriendThemself {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        User friend = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (user.getUserId().equals(friend.getUserId())) {
            throw new UserCannotFriendThemself();
        }

        if (friendshipRepository.existsByUserIdAndFriendId(user.getUserId(), friend.getUserId())) {
            throw new FriendshipAlreadyExistsException(user.getUserId(), friend.getUserId());
        }
        Friendship friendship = new Friendship(user, friend, FriendshipStatus.PENDING);

        return this.friendshipDtoMapper.toFriendshipDto(friendshipRepository.save(friendship), userId);
    }

    public FriendshipDto updateFriendshipStatus(String username, Long userId, FriendshipStatus status) throws FriendshipNotFoundException, UserNotFoundException {
        User friend = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Friendship friendship = getFriendship(friend, user);

        friendship.setStatus(status);
        return this.friendshipDtoMapper.toFriendshipDto(friendshipRepository.save(friendship), userId);
    }

    public List<FriendshipDto> getFriendsByStatus(Long userId, FriendshipStatus status) {
        List<Friendship> friendships = friendshipRepository.findFriendshipsByUserAndStatus(userId, status);

        return friendships.stream()
                .map(friendship -> friendshipDtoMapper.toFriendshipDto(friendship, userId))
                .collect(Collectors.toList());
    }

    public void deleteFriend(Long userId, String username) throws UserNotFoundException, FriendshipNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        User friend = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        Friendship friendship = findFriendshipInBothDirections(user, friend);
        friendshipRepository.delete(friendship);
    }

    public Friendship getFriendship(User user, User friend) throws FriendshipNotFoundException {
        Friendship friendship = friendshipRepository.findByUserIdAndFriendId(user.getUserId(), friend.getUserId());
        if (friendship == null) {
            throw new FriendshipNotFoundException(user.getUserId(), friend.getUserId());
        }
        return friendship;
    }

    //Helper function for getting a friendship in both directions
    private Friendship findFriendshipInBothDirections(User user, User friend) throws FriendshipNotFoundException {
        Friendship friendship = friendshipRepository.findByUserIdAndFriendId(user.getUserId(), friend.getUserId());
        if (friendship == null) {
            friendship = friendshipRepository.findByUserIdAndFriendId(friend.getUserId(), user.getUserId());
        }
        if (friendship == null) {
            throw new FriendshipNotFoundException(user.getUserId(), friend.getUserId());
        }
        return friendship;
    }
}
