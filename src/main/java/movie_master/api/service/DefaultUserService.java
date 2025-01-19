package movie_master.api.service;

import movie_master.api.dto.UserDto;
import movie_master.api.dto.UserMovie.UserMovieDto;
import movie_master.api.exception.*;
import movie_master.api.mapper.UserDtoMapper;
import movie_master.api.mapper.UserMovieDtoMapper;
import movie_master.api.model.Movie;
import movie_master.api.model.PasswordResetToken;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.model.role.Role;
import movie_master.api.repository.*;
import movie_master.api.request.UpdateUserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The default implementation for the user service.
 */
@Service
public class DefaultUserService implements UserService {

    // Repositories
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final UserMovieRepository userMovieRepository;
    private final FriendshipRepository friendshipRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    // Utilities
    private final PasswordEncoder passwordEncoder;
    private final UserDtoMapper userDtoMapper;
    private final UserMovieDtoMapper userMovieDtoMapper;

    // Service
    private final EmailService emailService;

    @Value("${client.host}")
    private String clientHost;

    public DefaultUserService(
            UserRepository userRepository, MovieRepository movieRepository,
            UserMovieRepository userMovieRepository,
            FriendshipRepository friendshipRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder, UserDtoMapper userDtoMapper, UserMovieDtoMapper userMovieDtoMapper,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.userMovieRepository = userMovieRepository;
        this.friendshipRepository = friendshipRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDtoMapper = userDtoMapper;
        this.userMovieDtoMapper = userMovieDtoMapper;
        this.emailService = emailService;
    }

    /**
     * Registers a new user using a given
     * RegisterUserRequest object.
     */
    @Override
    public UserDto register(String email, String username, String password)
            throws EmailTakenException, UsernameTakenException {

        // Check if the given email or password already exists somewhere
        Optional<User> userFoundByEmail = this.userRepository
                .findByEmail(email);
        Optional<User> userFoundByUsername = this.userRepository
                .findByUsername(username);

        // If so, throw an exception
        if (userFoundByEmail.isPresent()) {
            throw new EmailTakenException(email);
        }

        if (userFoundByUsername.isPresent()) {
            throw new UsernameTakenException(username);
        }

        // Save the result and map the user object to a DTO before returning
        User createdUser = this.userRepository.save(
                new User(
                        email,
                        username,
                        passwordEncoder.encode(password),
                        Role.ROLE_USER,
                        true,
                        false
                )
        );

        return this.userDtoMapper.apply(createdUser);
    }

    /**
     * Deletes a user. Removing all traces.
     *
     * @param userId id of the user
     */
    @Override
    public void deleteUserById(Long userId) throws UserNotFoundException {
        if (!this.userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        friendshipRepository.deleteFriendshipByUser(userId);
        userRepository.deleteById(userId);
    }

    /**
     * Retrieves the watchlist of a given user.
     *
     * @param userId id of the user
     * @return A set of UserMovie objects representing the
     * watchlist of this user.
     */
    @Override
    public Set<UserMovieDto> getWatchList(Long userId) throws UserNotFoundException {

        // Retrieve the user in Optional form
        Optional<User> user = userRepository.findById(userId);

        // If it does not exist, throw an exception. Otherwise,
        // return the watchlist.
        if (user.isPresent()) {
            return user.get().getWatchList().stream()
                .map(userMovieDtoMapper::mapUserMovieToDto)
                .collect(Collectors.toSet());
        } else {
            throw new UserNotFoundException(userId);
        }
    }

    /**
     * Retrieves a watchlist item related to a specific user and movie
     *
     * @param userId id of the user
     * @param movieId id of the movie
     * @return watchlist item of the specified user and movie
     */
    @Override
    public UserMovieDto getWatchListItem(Long userId, Long movieId) throws UserNotFoundException {
        // Retrieve the user in Optional form
        Optional<User> user = userRepository.findById(userId);

        // If it does not exist, throw an exception. Otherwise,
        // return the watchlist.
        if (user.isPresent()) {
            return user.get()
                    .getWatchList()
                    .stream()
                    .filter(userMovie -> userMovie.getMovie().getId() == movieId)
                    .map(userMovieDtoMapper::mapUserMovieToDto)
                    .findAny().orElse(null);
        } else {
            throw new UserNotFoundException(userId);
        }
    }

    /**
     * Adds a movie to the watchlist of a user.
     *
     * @param userId  id of the user
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
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException(userId);
        } else if (movieOpt.isEmpty()) {
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

    /**
     * Removes a movie from a users watchlist.
     *
     * @param userId  id of the user
     * @param movieId id of the movie
     */
    @Override
    public void removeMovieFromWatchlist(Long userId, Long movieId)
            throws UserNotFoundException, UserMovieNotFoundException {

        // Retrieve movie objects
        Optional<User> userOpt = userRepository.findById(userId);

        // Check whether both entities exist
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        User user = userOpt.get();

        boolean hasWatchlisted = user.getWatchList()
                .stream().anyMatch(e -> e.getMovie().getId() == movieId);

        if (!hasWatchlisted) {
            throw new UserMovieNotFoundException(movieId);
        }

        UserMovie userMovie = user.getWatchList().stream()
        .filter(e -> e.getMovie().getId() == movieId)
        .collect(Collectors.toList()).get(0);
        
        user.getWatchList().remove(userMovie);
        userMovieRepository.delete(userMovie);
    }

    /**
     * For a given user, updates a specific movies' 'watched'
     * status to either watched or unwatched.
     *
     * @param userId of the user
     * @param userId of the movie
     * @returns UserMovie object containing the updated
     * state of the users' relationship with the movie.
     */
    @Override
    public UserMovie updateWatchItemStatus(Long userId, Long movieId, boolean watched)
            throws UserNotFoundException, UserMovieNotFoundException {

        // Retrieve the user
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        User user = userOpt.get();

        // Retrieve the watchlistitem using its ID (not the movie id!)
        Optional<UserMovie> watchlistItemOpt = user.getWatchList()
                .stream().filter(userMovieOpt -> userMovieOpt.getMovie().getId()
                        == movieId).findFirst();
        if (watchlistItemOpt.isEmpty()) {
            throw new UserMovieNotFoundException(movieId);
        }

        // Set the new 'watched' value and return the updated UserMovie
        UserMovie watchlistItem = watchlistItemOpt.get();
        watchlistItem.setWatched(watched);
        userRepository.save(user);
        return watchlistItem;
    }

    @Override
    public UserDto getUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UserNotFoundException(username);
        }
        return this.userDtoMapper.apply(user.get());
    }

    @Override
    public UserDto getUserByEmail(String email) throws EmailNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new EmailNotFoundException(email);
        }
        return this.userDtoMapper.apply(user.get());
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> foundUsers = this.userRepository.findAll();
        List<UserDto> users = new ArrayList<>();

        for (User user : foundUsers) {
            users.add(this.userDtoMapper.apply(user));
        }
        return users;
    }

    @Override
    public User findById(Long userId) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        return user.get();
    }

    private boolean emailIsTakenByDifferentUser(String email, Long userId) {
        Optional<User> userFoundByEmail = this.userRepository
                .findByEmail(email);
        return userFoundByEmail.filter(user -> !user.getUserId().equals(userId)).isPresent();
    }

    private boolean usernameIsTakenByDifferentUser(String username, Long userId) {
        Optional<User> userFoundByUsername = this.userRepository
                .findByUsername(username);
        return userFoundByUsername.filter(user -> !user.getUserId().equals(userId)).isPresent();
    }

    @Override
    public UserDto updateUser(Long userId,
                              UpdateUserRequest updateUserRequest,
                              Long jwtUserId,
                              Role jwtUserRole)
            throws UnauthorizedException,
            EmailTakenException,
            UsernameTakenException,
            UserNotFoundException {
        // Only allowing the user itself to update
        if (!Objects.equals(userId, jwtUserId)) {
            throw new UnauthorizedException();
        }

        if (emailIsTakenByDifferentUser(updateUserRequest.email(), userId)) {
            throw new EmailTakenException(updateUserRequest.email());
        }

        if (usernameIsTakenByDifferentUser(updateUserRequest.username(), userId)) {
            throw new UsernameTakenException(updateUserRequest.username());
        }

        User updatedUser = userRepository
                .findById(userId)
                .map(user -> {
                    if (updateUserRequest.username() != null) {
                        user.setUsername(updateUserRequest.username());
                    }
                    if (updateUserRequest.email() != null) {
                        user.setEmail(updateUserRequest.email());
                    }
                    if (updateUserRequest.profilePicture() != null) {
                        user.setProfilePicture(updateUserRequest.profilePicture());
                    }
                    return userRepository.save(user);
                })
                .orElseThrow(UserNotFoundException::new);

        return this.userDtoMapper.apply(updatedUser);
    }

    @Override
    public UserDto updateUserRole(Long userId, String role, Role jwtUserRole) throws UserNotFoundException {
        User updatedUser = userRepository
                .findById(userId)
                .map(user -> {
                    if (role.equalsIgnoreCase(Role.ROLE_MOD.toString())) {
                        user.setRole(Role.ROLE_MOD);
                    } else {
                        user.setRole(Role.ROLE_USER);
                    }
                    return userRepository.save(user);
                })
                .orElseThrow(UserNotFoundException::new);

        return this.userDtoMapper.apply(updatedUser);
    }

    /**
     * Updates the 'banned' status of a user. Note that this is meant to be 
     * exclusively used by endpoints controlled by administrators.
     */
    @Override
    public User updateUserBannedStatus(Long userId, boolean banned) throws UserNotFoundException {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        User user = userOpt.get();
        user.setBanned(banned);
        userRepository.save(user);

        return user;
    }

    /**
     * Creates a password reset token for the user, if the user does not have one
     * Also, it sends the user an email that contains instructions for resetting their password
     */
    @Override
    public void requestPasswordReset(String email) throws EmailNotFoundException, UserAlreadyHasPasswordResetToken {
        Optional<User> userOpt = userRepository.findByEmail(email);

        // If there is no user with that email, throw an exception
        if (userOpt.isEmpty()) {
            throw new EmailNotFoundException(email);
        }

        User user = userOpt.get();

        // Find the password reset token of the user
        Optional<PasswordResetToken> passwordResetTokenOpt = passwordResetTokenRepository.findByUser(user);

        // If there is already a password reset token, and it is still valid, throw an exception
        if (passwordResetTokenOpt.isPresent() && passwordResetTokenOpt.get().hasNotExpired()) {
            throw new UserAlreadyHasPasswordResetToken();
        } else if (passwordResetTokenOpt.isPresent() && !passwordResetTokenOpt.get().hasNotExpired()) {
            // remove the invalid password reset token of the user so that a new one can be created
            passwordResetTokenRepository.delete(passwordResetTokenOpt.get());
        }

        PasswordResetToken passwordResetToken = new PasswordResetToken();

        passwordResetToken.setUser(user);

        passwordResetTokenRepository.save(passwordResetToken);

        emailService.sendEmail(user.getEmail(),
                "Movie Master Password Reset Link",
                ("<h1>Reset your password</h1>" +
                        "<p>Click on this link in order to reset your password.</p>" +
                        "<p><b>Please note that this link is only valid for 15 minutes.</b>" +
                        "</p>%s/signin/password/reset?passwordResetToken=%s</p>")
                        .formatted(clientHost, passwordResetToken));
    }

    /**
     * Resets the password of a user
     * In order to reset the password, the user needs to have a valid password reset token
     * Otherwise it will not be possible to reset their password
     */
    @Override
    public void resetPassword(String token, String newPassword) throws InvalidPasswordResetTokenException {
        Optional<PasswordResetToken> passwordResetTokenOpt = passwordResetTokenRepository.findByValue(token);

        // The given password reset token does not exist
        if (passwordResetTokenOpt.isEmpty()) {
            throw new InvalidPasswordResetTokenException();
        }

        PasswordResetToken passwordResetToken = passwordResetTokenOpt.get();

        // The given password reset token has expired
        if (!passwordResetToken.hasNotExpired()) {
            throw new InvalidPasswordResetTokenException();
        }

        User user = passwordResetToken.getUser();

        // encode the new password
        String newEncodedPassword = passwordEncoder.encode(newPassword);

        user.setPassword(newEncodedPassword);

        // save the changes
        userRepository.save(user);

        // remove the used reset password token
        passwordResetTokenRepository.delete(passwordResetToken);
    }
}
