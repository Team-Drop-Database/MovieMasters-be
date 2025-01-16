package movie_master.api.service;

import movie_master.api.dto.UserDto;
import movie_master.api.dto.UserMovie.UserMovieDto;
import movie_master.api.exception.*;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.model.role.Role;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.request.UpdateUserRequest;

import java.util.List;
import java.util.Set;

/**
 * Interface for user services
 */
public interface UserService {
    UserDto register(RegisterUserRequest registerUserRequest) throws EmailTakenException, UsernameTakenException;
    UserDto getUserByEmail(String email) throws EmailNotFoundException;
    UserDto getUserByUsername(String username) throws UserNotFoundException;
    void deleteUserById(Long userId) throws UserNotFoundException;
    User updateUserBannedStatus(Long userId, boolean banned) throws UserNotFoundException;
    Set<UserMovieDto> getWatchList(Long userId) throws UserNotFoundException;
    UserMovieDto getWatchListItem(Long userId, Long movieId) throws UserNotFoundException;
    User findById(Long userId) throws UserNotFoundException;
    List<UserDto> getAllUsers();
    UserDto updateUser(Long userId, UpdateUserRequest updateUserRequest, Long jwtUserId, Role jwtUserRole) throws UnauthorizedException, EmailTakenException, UsernameTakenException, UserNotFoundException;
    UserDto updateUserRole(Long  userId, String role, Role jwtUserRole) throws UserNotFoundException;
    UserMovie addMovieToWatchlist(Long userId, Long movieId) throws UserNotFoundException, MovieNotFoundException;
    void removeMovieFromWatchlist(Long userId, Long movieId) throws UserNotFoundException, UserMovieNotFoundException;
    UserMovie updateWatchItemStatus(Long userId, Long movieId, boolean watched) throws UserNotFoundException, UserMovieNotFoundException;
}
