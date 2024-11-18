package movie_master.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import movie_master.model.Movie;

import movie_master.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Set<Movie> getWatchList(long userId){
        return userRepository.findById(userId).getWatchList();
    }
}
