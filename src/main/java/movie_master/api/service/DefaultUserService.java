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
 * The default implementation for the user service.
 */
@Service
public class DefaultUserService implements UserService {

    // Repositorites
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
        throws EmailHasAlreadyBeenTaken, UsernameHasAlreadyBeenTaken {

        // Check if the given email or password already exists somewhere
        Optional<User> userFoundByEmail = this.userRepository
            .findByEmail(registerUserRequest.email());
        Optional<User> userFoundByUsername = this.userRepository
            .findByUsername(registerUserRequest.username());

        // If so, throw an exception
        if (userFoundByEmail.isPresent()) {
            throw new EmailHasAlreadyBeenTaken(registerUserRequest.email());
        }

        if (userFoundByUsername.isPresent()) {
            throw new UsernameHasAlreadyBeenTaken(registerUserRequest.username());
        }

        // Create a new user object, algorithmatically encode the password
        User userToCreate = new User(
                registerUserRequest.email(),
                registerUserRequest.username(),
                passwordEncoder.encode(registerUserRequest.password()),
                Roles.USER.name(),
                true);

        // Save the result and map the user object to a DTO before returning
        this.userRepository.save(userToCreate);
        return this.userDtoMapper.apply(userToCreate);
    }

    /**
     * Retrieves the watchlist of a given user.
     * 
     * @param userId id of the user
     * @return A set of UserMovie objects representing the
     *  watchlist of this user.
     */
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
        UserMovie movieAssociation = new UserMovie(user, movie, false, -1.0);
        user.addMovieToWatchlist(movieAssociation);

        // Save the newly updated association and return it
        userRepository.save(user);
        return movieAssociation;
    }
}
