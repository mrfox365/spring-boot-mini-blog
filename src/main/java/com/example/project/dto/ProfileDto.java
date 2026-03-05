package com.example.project.dto;

import java.time.LocalDate;
import lombok.Data;

/**
 * Class containing DTOs for profile operations.
 */
public class ProfileDto {

  /**
   * Response containing public profile data.
   */
  @Data
  public static class UserProfileResponse {
    private String username;
    private String email;
    private String bio;
    private LocalDate birthDate;
  }

  /**
   * Request to update profile settings.
   */
  @Data
  public static class UpdateProfileRequest {
    private String currentUsername;
    private String newUsername;
    private String newPassword;
    private String bio;
    private LocalDate birthDate;
  }
}