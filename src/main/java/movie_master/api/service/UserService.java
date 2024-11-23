package movie_master.api.service;

import java.util.Set;

import movie_master.api.dto.UserDto;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.exception.EmailHasAlreadyBeenTaken;
import movie_master.api.exception.MovieNotFoundException;
import movie_master.api.exception.UserMovieNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.exception.UsernameHasAlreadyBeenTaken;
import movie_master.api.model.UserMovie;

/**
 * Interface for user services
 */
public interface UserService {
    UserDto register(RegisterUserRequest registerUserRequest) throws EmailHasAlreadyBeenTaken, UsernameHasAlreadyBeenTaken;
    Set<UserMovie> getWatchList(Long userId) throws UserNotFoundException;
    UserMovie addMovieToWatchlist(Long userId, Long movieId) throws UserNotFoundException, MovieNotFoundException;
    UserMovie updateWatchItemStatus(Long userId, Long itemId, boolean watched) throws UserNotFoundException, UserMovieNotFoundException;
}