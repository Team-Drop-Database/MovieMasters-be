package movie_master.api.service;

import movie_master.api.dto.UserDto;
import movie_master.api.exception.EmailHasAlreadyBeenTaken;
import movie_master.api.exception.MovieNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.exception.UsernameHasAlreadyBeenTaken;
import movie_master.api.mapper.UserDtoMapper;
import movie_master.api.model.Movie;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.model.role.Roles;
import movie_master.api.repository.MovieRepository;
import movie_master.api.repository.UserRepository;
import movie_master.api.request.RegisterUserRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;

/**
 * The default implementation for the user service
 */
@Service
public class DefaultUserService implements UserService {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDtoMapper userDtoMapper;

    public DefaultUserService(
        UserRepository userRepository, MovieRepository movieRepository,
         PasswordEncoder passwordEncoder, UserDtoMapper userDtoMapper) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public UserDto register(RegisterUserRequest registerUserRequest) 
        throws EmailHasAlreadyBeenTaken, UsernameHasAlreadyBeenTaken {
        Optional<User> userFoundByEmail = this.userRepository
            .findByEmail(registerUserRequest.email());
        Optional<User> userFoundByUsername = this.userRepository
            .findByUsername(registerUserRequest.username());

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

    @Override
    public Set<UserMovie> getWatchList(Long userId) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(userId);

        if(user.isPresent()){
            return user.get().getWatchList();
        } else {
            throw new UserNotFoundException(userId);
        }
    }

    @Override
    public UserMovie addMovieToWatchlist(Long userId, Long movieId)
            throws UserNotFoundException, MovieNotFoundException {
        
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Movie> movieOpt = movieRepository.findById(movieId);

        if(userOpt.isEmpty()) {
            throw new UserNotFoundException(userId);
        } else if(movieOpt.isEmpty()) {
            throw new MovieNotFoundException(movieId);
        }

        User user = userOpt.get();
        Movie movie = movieOpt.get();

        UserMovie movieAssociation = new UserMovie(user, movie, false, -1.0);
        user.addMovieToWatchlist(movieAssociation);

        userRepository.save(user);

        return movieAssociation;
    }
}
