package movie_master.api.service;

import movie_master.api.dto.FriendshipDto;
import movie_master.api.exception.FriendshipAlreadyExistsException;
import movie_master.api.exception.FriendshipNotFoundException;
import movie_master.api.exception.UserCannotFriendThemself;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.model.Friendship;
import movie_master.api.model.User;
import movie_master.api.model.friendship.FriendshipStatus;

import java.util.List;

public interface FriendshipService {
    FriendshipDto addFriend(Long userId, String username) throws FriendshipAlreadyExistsException, UserNotFoundException, UserCannotFriendThemself;

    FriendshipDto updateFriendshipStatus(String username, Long userId, FriendshipStatus status) throws FriendshipNotFoundException, UserNotFoundException;

    List<FriendshipDto> getFriendsByStatus(Long userId, FriendshipStatus status);

    void deleteFriend(Long userId, String username) throws FriendshipNotFoundException, UserNotFoundException;
}
