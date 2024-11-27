package movie_master.api.service;

import movie_master.api.dto.UserDto;
import movie_master.api.exception.EmailTakenException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.exception.UsernameTakenException;
import movie_master.api.mapper.UserDtoMapper;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.model.role.Roles;
import movie_master.api.repository.UserRepository;
import movie_master.api.request.RegisterUserRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;

/**
 * The implementation for the user service
 */
@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDtoMapper userDtoMapper;

    public DefaultUserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserDtoMapper userDtoMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public UserDto register(RegisterUserRequest registerUserRequest) throws EmailTakenException, UsernameTakenException {
        Optional<User> userFoundByEmail = userRepository.findByEmail(registerUserRequest.email());
        Optional<User> userFoundByUsername = userRepository.findByUsername(registerUserRequest.username());

        if (userFoundByEmail.isPresent()) {
            throw new EmailTakenException(registerUserRequest.email());
        }

        if (userFoundByUsername.isPresent()) {
            throw new UsernameTakenException(registerUserRequest.username());
        }

        String encodedPassword = passwordEncoder.encode(registerUserRequest.password());

        User userToCreate = new User(
            registerUserRequest.email(),
            registerUserRequest.username(),
            encodedPassword,
            Roles.USER.name(),
            true
        );

        User createdUser = userRepository.save(userToCreate);

        return userDtoMapper.apply(createdUser);
    }

    @Override
    public Set<UserMovie> getWatchList(Long userId) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            return user.get().getWatchList();
        } else {
            throw new UserNotFoundException(userId);
        }
    }
}
