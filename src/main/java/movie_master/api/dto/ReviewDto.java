package movie_master.api.dto;

public record ReviewDto(
    long id,
    String username,
    String userProfilePicture,
    String movieTitle,
    double rating,
    String reviewBody
) {}
