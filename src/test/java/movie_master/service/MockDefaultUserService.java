package movie_master.service;

import movie_master.api.dto.UserDto;
import movie_master.api.exception.EmailHasAlreadyBeenTaken;
import movie_master.api.exception.MovieNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.exception.UsernameHasAlreadyBeenTaken;
import movie_master.api.mapper.UserDtoMapper;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.model.role.Roles;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Set;

/**
 * A mock implementation of RegisterUserService for testing purpose
 */
public class MockDefaultUserService implements UserService {
    private List<UserDto> registeredUsers;
    private PasswordEncoder passwordEncoder;
    private UserDtoMapper userDtoMapper;

    public MockDefaultUserService(List<UserDto> registeredUsers, PasswordEncoder passwordEncoder, UserDtoMapper userDtoMapper) {
        this.registeredUsers = registeredUsers;
        this.passwordEncoder = passwordEncoder;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public UserDto register(RegisterUserRequest registerUserRequest) throws EmailHasAlreadyBeenTaken, UsernameHasAlreadyBeenTaken {

        if (registeredUsers.stream().anyMatch(userDto -> userDto.email().equals(registerUserRequest.email()))) {
            throw new EmailHasAlreadyBeenTaken(registerUserRequest.email());
        }

        if (registeredUsers.stream().anyMatch(userDto -> userDto.username().equals(registerUserRequest.username()))) {
            throw new UsernameHasAlreadyBeenTaken(registerUserRequest.username());
        }

        User userToCreate = new User(
                registerUserRequest.email(),
                registerUserRequest.username(),
                passwordEncoder.encode(registerUserRequest.password()),
                Roles.USER.name(),
                true);

        UserDto userDto = userDtoMapper.apply(userToCreate);

        registeredUsers.add(userDto);

        return userDto;
    }

    @Override
    public void deleteUserById(Long userId) throws UserNotFoundException {
        // TODO: auto-generated method stub, not in use
        throw new UnsupportedOperationException("Unimplemented method 'deleteUserById'");
    }

    public List<UserDto> getRegisteredUsers() {
        return registeredUsers;
    }

    @Override
    public Set<UserMovie> getWatchList(Long userId) throws UserNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getWatchList'");
    }

    @Override
    public UserMovie addMovieToWatchlist(Long userId, Long movieId)
            throws UserNotFoundException, MovieNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addMovieToWatchlist'");
    }
}
