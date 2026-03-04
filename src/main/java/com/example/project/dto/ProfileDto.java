package com.example.project.dto;
import lombok.Data;
import java.time.LocalDate;

public class ProfileDto {

    @Data
    public static class UserProfileResponse {
        private String username;
        private String email;
        private String bio;
        private LocalDate birthDate;
    }

    @Data
    public static class UpdateProfileRequest {
        private String currentUsername;
        private String newUsername;
        private String newPassword;
        private String bio;
        private LocalDate birthDate;
    }
}