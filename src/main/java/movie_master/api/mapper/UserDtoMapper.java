package movie_master.api.mapper;

import movie_master.api.model.User;
import movie_master.api.dto.UserDto;
import org.springframework.stereotype.Service;
import java.util.function.Function;

/**
 * Class that contains a function that maps a user object to a user dto object
 * A data transfer object is being used to control which data of a model
 * will be exposed to the client.
 */
@Service
public class UserDtoMapper implements Function<User, UserDto> {

    @Override
    public UserDto apply(User user) {
        return new UserDto(
                user.getUserId(),
                user.getEmail(),
                user.getUsername(),
                user.getProfilePicture(),
                user.getDateJoined(),
                user.getRoles());
    }
}
