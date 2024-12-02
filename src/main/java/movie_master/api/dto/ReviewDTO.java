package movie_master.api.dto;

public record ReviewDTO(
    long id,
    String username,
    String userProfilePicture,
    String movieTitle,
    double rating,
    String reviewBody
) {}
