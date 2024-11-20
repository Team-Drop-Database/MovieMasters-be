package movie_master.api.service;

import java.util.Set;

import movie_master.api.dto.UserDto;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.exception.EmailHasAlreadyBeenTaken;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.exception.UsernameHasAlreadyBeenTaken;
import movie_master.api.model.UserMovie;

/**
 * Interface for user services
 */
public interface UserService {
    UserDto register(RegisterUserRequest registerUserRequest) throws EmailHasAlreadyBeenTaken, UsernameHasAlreadyBeenTaken;
    Set<UserMovie> getWatchList(Long userId) throws UserNotFoundException;
}

// package movie_master.api.service;

// import java.util.Optional;
// import java.util.Set;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import movie_master.api.exception.UserNotFoundException;
// import movie_master.api.model.User;
// import movie_master.api.model.UserMovie;
// import movie_master.api.repository.UserRepository;

// @Service
// public class UserService {

//     @Autowired
//     private UserRepository userRepository;

    // public Set<UserMovie> getWatchList(Long userId) throws UserNotFoundException {
    //     Optional<User> user = userRepository.findById(userId);
    //     if(user.isPresent())
    //         return userRepository.findById(userId).get().getWatchList();
    //     else
    //         throw new UserNotFoundException(userId);
    // }
// }
