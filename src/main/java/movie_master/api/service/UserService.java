package movie_master.api.service;

import movie_master.api.dto.UserDto;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.exception.EmailTakenException;
import movie_master.api.exception.MovieNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.exception.UsernameTakenException;
import movie_master.api.model.UserMovie;
import movie_master.api.request.RegisterUserRequest;

import java.util.Set;

/**
 * Interface for user services
 */
public interface UserService {
    UserDto register(RegisterUserRequest registerUserRequest) throws EmailTakenException, UsernameTakenException;
    void deleteUserById(Long userId) throws UserNotFoundException;
    Set<UserMovie> getWatchList(Long userId) throws UserNotFoundException;
    UserMovie addMovieToWatchlist(Long userId, Long movieId) throws UserNotFoundException, MovieNotFoundException;
}
