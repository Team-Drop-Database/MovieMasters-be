package movie_master.api.request;

public record UpdateUserRequest (
        String username,
        String email,
        String profilePicture
) {}
