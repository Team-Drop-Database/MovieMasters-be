package movie_master.api.request;

import jakarta.validation.constraints.NotNull;
import movie_master.api.model.role.Role;

public record UpdateUserRequest (
        @NotNull String userName,
        @NotNull String email,
        String profilePicture,
        Role role
) {}
