package movie_master.api.service;

import movie_master.api.dto.UserDto;
import movie_master.api.exception.EmailTakenException;
import movie_master.api.exception.MovieNotFoundException;
import movie_master.api.exception.UserMovieNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.exception.UsernameTakenException;
import movie_master.api.mapper.UserDtoMapper;
import movie_master.api.model.Movie;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.model.role.Role;
import movie_master.api.repository.MovieRepository;
import movie_master.api.repository.UserRepository;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.request.UpdateUserRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
                        Role.USER,
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
        UserMovie movieAssociation = new UserMovie(user, movie, false);
        user.addMovieToWatchlist(movieAssociation);

        // Save the newly updated association and return it
        userRepository.save(user);
        return movieAssociation;
    }

    @Override
    public UserMovie updateWatchItemStatus(Long userId, Long itemId, boolean watched) 
            throws UserNotFoundException, UserMovieNotFoundException {

        // Retrieve the user
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        User user = userOpt.get();

        // Retrieve the watchlistitem using its ID (not the movie id!)
        Optional<UserMovie> watchlistItemOpt = user.getWatchList()
            .stream().filter(userMovieOpt -> userMovieOpt.getId()
            .equals(itemId)).findFirst();
        if (watchlistItemOpt.isEmpty()) {
            throw new UserMovieNotFoundException(itemId);
        }
        
        // Set the new 'watched' value and return the updated UserMovie
        UserMovie watchlistItem = watchlistItemOpt.get();
        watchlistItem.setWatched(watched);
        userRepository.save(user);
        return watchlistItem;
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

    @Override
    public List<UserDto> getAllUsers() {
        List<User> foundUsers = this.userRepository.findAll();
        List<UserDto> users = new ArrayList<>();

        for (User user : foundUsers ) {
            users.add(this.userDtoMapper.apply(user));
        }
        return users;
    }

    @Override
    public User getUserById(Long userId) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        return user.get();
    }

    private Boolean validateExistingEmail(String email) {
        Optional<User> userFoundByEmail = this.userRepository
                .findByEmail(email);
        return userFoundByEmail.isPresent();
    }

    private Boolean validateExistingUsername(String username) {
        Optional<User> userFoundByUsername = this.userRepository
                .findByUsername(username);
        return userFoundByUsername.isPresent();
    }

    @Override
    public UserDto updateUser(Long userId, UpdateUserRequest updateUserRequest) throws UserNotFoundException, EmailTakenException, UsernameTakenException {
        if (validateExistingEmail(updateUserRequest.email())) {
            throw new EmailTakenException(updateUserRequest.email());
        }

        if (validateExistingUsername(updateUserRequest.username())) {
            throw new UsernameTakenException(updateUserRequest.username());
        }

        User updatedUser = userRepository.findById(userId).map(user -> {
            user.setUsername(updateUserRequest.username());
            user.setEmail(updateUserRequest.email());
            user.setProfilePicture(updateUserRequest.profilePicture());
            if (updateUserRequest.role() != null) {
                user.setRole(updateUserRequest.role());
            }
            return userRepository.save(user);
        }).orElseThrow(UserNotFoundException::new);

        return this.userDtoMapper.apply(updatedUser);
    }

    @Override
    public UserDto updateUserRole(Long userId, String role) throws UserNotFoundException {
        User updatedUser = userRepository.findById(userId).map(user -> {
            if (role.equalsIgnoreCase("mod")) {
                user.setRole(Role.MOD);
            } else {
                user.setRole(Role.USER);
            }
            return userRepository.save(user);
        }).orElseThrow(UserNotFoundException::new);

        return this.userDtoMapper.apply(updatedUser);
    }
}
