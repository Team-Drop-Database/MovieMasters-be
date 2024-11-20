package movie_master.api.service;

import movie_master.api.dto.UserDto;
import movie_master.api.exception.EmailHasAlreadyBeenTaken;
import movie_master.api.exception.UsernameHasAlreadyBeenTaken;
import movie_master.api.mapper.UserDtoMapper;
import movie_master.api.model.User;
import movie_master.api.model.role.Roles;
import movie_master.api.repository.UserRepository;
import movie_master.api.request.RegisterUserRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * The default implementation for the user service
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
    public UserDto register(RegisterUserRequest registerUserRequest) throws EmailHasAlreadyBeenTaken, UsernameHasAlreadyBeenTaken {
        Optional<User> userFoundByEmail = this.userRepository.findByEmail(registerUserRequest.email());
        Optional<User> userFoundByUsername = this.userRepository.findByUsername(registerUserRequest.username());

        if (userFoundByEmail.isPresent()) {
            throw new EmailHasAlreadyBeenTaken(registerUserRequest.email());
        }

        if (userFoundByUsername.isPresent()) {
            throw new UsernameHasAlreadyBeenTaken(registerUserRequest.username());
        }

        User userToCreate = new User(
                registerUserRequest.email(),
                registerUserRequest.username(),
                passwordEncoder.encode(registerUserRequest.password()),
                Roles.USER.name(),
                true);

        this.userRepository.save(userToCreate);

        return this.userDtoMapper.apply(userToCreate);
    }
}
