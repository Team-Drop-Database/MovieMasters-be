package movie_master.api.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import movie_master.api.exception.UserNotFoundException;
import movie_master.api.model.User;
import movie_master.api.model.UserMovie;
import movie_master.api.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Set<UserMovie> getWatchList(Long userId) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent())
            return userRepository.findById(userId).get().getWatchList();
        else
            throw new UserNotFoundException(userId);
    }
}
