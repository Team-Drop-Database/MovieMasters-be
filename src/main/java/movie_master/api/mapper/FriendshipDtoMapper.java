package movie_master.api.mapper;

import movie_master.api.dto.FriendshipDto;
import movie_master.api.model.Friendship;
import org.springframework.stereotype.Service;

/**
 * Class that contains a function that maps a friendship object to a friendship dto object
 * A data transfer object is being used to control which data of a model
 * will be exposed to the client.
 */
@Service
public class FriendshipDtoMapper {

    public FriendshipDto toFriendshipDto(Friendship friendship, Long userId) {
        boolean isUserFirst = friendship.getUser().getUserId().equals(userId);

        String username = friendship.getUser().getUsername();
        String friendUsername = isUserFirst ? friendship.getFriend().getUsername() : friendship.getUser().getUsername();
        String friendProfilePicture = isUserFirst ? friendship.getFriend().getProfilePicture() : friendship.getUser().getProfilePicture();

        return new FriendshipDto(
                friendship.getId(),
                username,
                friendUsername,
                friendProfilePicture,
                friendship.getStatus(),
                friendship.getFriendshipDate()
        );
    }
}