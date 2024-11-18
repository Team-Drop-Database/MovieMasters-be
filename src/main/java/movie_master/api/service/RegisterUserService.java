package movie_master.api.service;

import movie_master.api.dto.UserDto;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.exception.EmailHasAlreadyBeenTaken;
import movie_master.api.exception.UsernameHasAlreadyBeenTaken;

/**
 * Interface for services that register a user
 */
public interface RegisterUserService {
    UserDto registerUser(RegisterUserRequest registerUserRequest) throws EmailHasAlreadyBeenTaken, UsernameHasAlreadyBeenTaken;
}
