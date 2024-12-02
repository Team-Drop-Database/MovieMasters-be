package movie_master.api.service;

import movie_master.api.dto.UserDto;
import movie_master.api.exception.EmailTakenException;
import movie_master.api.exception.MovieNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.exception.UsernameTakenException;
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
 * The default implementation for the user service.
 */
@Service
public class DefaultUserService implements UserService {

    // Repositories
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    // Utilities
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

    /**
     * Registers a new user using a given 
     * RegisterUserRequest object.
     */
    @Override
    public UserDto register(RegisterUserRequest registerUserRequest) 
        throws EmailTakenException, UsernameTakenException {

        // Check if the given email or password already exists somewhere
        Optional<User> userFoundByEmail = this.userRepository
            .findByEmail(registerUserRequest.email());
        Optional<User> userFoundByUsername = this.userRepository
            .findByUsername(registerUserRequest.username());

        // If so, throw an exception
        if (userFoundByEmail.isPresent()) {
            throw new EmailTakenException(registerUserRequest.email());
        }

        if (userFoundByUsername.isPresent()) {
            throw new UsernameTakenException(registerUserRequest.username());
        }

        // Save the result and map the user object to a DTO before returning
        User createdUser = this.userRepository.save(
                new User(
                        registerUserRequest.email(),
                        registerUserRequest.username(),
                        passwordEncoder.encode(registerUserRequest.password()),
                        Roles.USER.name(),
                        true
                        )
        );

        return this.userDtoMapper.apply(createdUser);
    }

    /**
     * Retrieves the watchlist of a given user.
     * 
     * @param userId id of the user
     * @return A set of UserMovie objects representing the
     *  watchlist of this user.
     */
    @Override
    public void deleteUserById(Long userId) throws UserNotFoundException {
        if (!this.userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public Set<UserMovie> getWatchList(Long userId) throws UserNotFoundException {

        // Retrieve the user in Optional form
        Optional<User> user = userRepository.findById(userId);

        // If it does not exist, throw an exception. Otherwise, 
        // return the watchlist.
        if(user.isPresent()){
            return user.get().getWatchList();
        } else {
            throw new UserNotFoundException(userId);
        }
    }

    /**
     * Adds a movie to the watchlist of a user.
     * 
     * @param userId id of the user
     * @param movieId id of the movie
     * @return new UserMovie object representing the association
     */
    @Override
    public UserMovie addMovieToWatchlist(Long userId, Long movieId)
            throws UserNotFoundException, MovieNotFoundException {
        
        // Retrieve movie objects
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Movie> movieOpt = movieRepository.findById(movieId);

        // Check whether both entities exist
        if(userOpt.isEmpty()) {
            throw new UserNotFoundException(userId);
        } else if(movieOpt.isEmpty()) {
            throw new MovieNotFoundException(movieId);
        }

        // Retrieve the entities in concrete form
        User user = userOpt.get();
        Movie movie = movieOpt.get();

        // Create the association
        UserMovie movieAssociation = new UserMovie(user, movie, false);
        user.addMovieToWatchlist(movieAssociation);

        // Save the newly updated association and return it
        userRepository.save(user);
        return movieAssociation;
    }

    @Override
    public UserDto getUserByName(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UserNotFoundException(username);
        }
        return this.userDtoMapper.apply(user.get());
    }

    @Override
    public UserDto getUserByEmail(String email) throws UserNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new UserNotFoundException(email);
        }
        return this.userDtoMapper.apply(user.get());
    }
}
