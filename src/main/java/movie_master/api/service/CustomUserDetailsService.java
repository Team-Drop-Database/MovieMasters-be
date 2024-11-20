package movie_master.api.service;

import movie_master.api.model.User;
import movie_master.api.model.detail.CustomUserDetails;
import movie_master.api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * This class is being used by the DAO Authentication provider
 * to retrieve the details of a user (username, roles, etc.)
 * See docs: <a href="https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/user-details-service.html#servlet-authentication-userdetailsservice">...</a>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        return user.map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
