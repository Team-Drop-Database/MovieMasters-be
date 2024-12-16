package movie_master.api.dto;

import movie_master.api.model.role.Role;

import java.time.LocalDate;

/**
 * Data transfer object that is being returned to the client
 * @param id
 * @param email
 * @param username
 * @param profile_picture
 * @param date_joined
 * @param role
 */
public record UserDto(Long id, String email, String username, String profile_picture, LocalDate date_joined, Role role) {}
