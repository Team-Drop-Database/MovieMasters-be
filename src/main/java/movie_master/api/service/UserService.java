package movie_master.api.service;

import movie_master.api.dto.UserDto;
import movie_master.api.model.User;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.exception.EmailTakenException;
import movie_master.api.exception.MovieNotFoundException;
import movie_master.api.exception.UserMovieNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.exception.UsernameTakenException;
import movie_master.api.model.UserMovie;

import java.util.List;
import java.util.Set;

/**
 * Interface for user services
 */
public interface UserService {
    UserDto register(RegisterUserRequest registerUserRequest) throws EmailTakenException, UsernameTakenException;
    UserDto getUserByName(String username) throws UserNotFoundException;
    UserDto getUserByEmail(String email) throws UserNotFoundException;
    User getUserById(Long id) throws UserNotFoundException;
    void deleteUserById(Long userId) throws UserNotFoundException;
    Set<UserMovie> getWatchList(Long userId) throws UserNotFoundException;
    List<UserDto> getAllUsers() throws UserNotFoundException;
    User updateUser(User user) throws UserNotFoundException;
    UserMovie addMovieToWatchlist(Long userId, Long movieId) throws UserNotFoundException, MovieNotFoundException;
    UserMovie updateWatchItemStatus(Long userId, Long itemId, boolean watched) throws UserNotFoundException, UserMovieNotFoundException;
}
