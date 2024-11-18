package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import movie_master.api.dto.UserDto;
import movie_master.api.request.RegisterUserRequest;
import movie_master.api.service.RegisterUserService;
import movie_master.api.exception.EmailHasAlreadyBeenTaken;
import movie_master.api.exception.UsernameHasAlreadyBeenTaken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

/**
 * Controller for registering users
 */
@RestController
@RequestMapping("/register")
public class RegisterController {
    private final RegisterUserService registerUserService;

    public RegisterController(RegisterUserService registerUserService) {
        this.registerUserService = registerUserService;
    }

    @PostMapping
    private ResponseEntity<Object> register(HttpServletRequest httpServletRequest, @Valid @RequestBody RegisterUserRequest registerUserRequest) {
        try {
            UserDto userDto = registerUserService.registerUser(registerUserRequest);
            return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI())).body(userDto);
        }
        catch (EmailHasAlreadyBeenTaken | UsernameHasAlreadyBeenTaken e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
