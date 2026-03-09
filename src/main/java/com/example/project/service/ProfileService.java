package com.example.project.service;

import com.example.project.dto.ProfileDto;
import com.example.project.entity.User;
import com.example.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing user profile information and updates.
 */
@Service
public class ProfileService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * Retrieves public profile data for a specific user.
   *
   * @param username the nickname of the user to look up
   * @return a DTO containing user's profile information
   * @throws IllegalArgumentException if the user is not found
   */
  public ProfileDto.UserProfileResponse getProfile(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    ProfileDto.UserProfileResponse dto = new ProfileDto.UserProfileResponse();
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setBio(user.getBio());
    dto.setBirthDate(user.getBirthDate());
    return dto;
  }

  /**
   * Updates profile information for the authenticated user.
   *
   * @param currentUsername the current nickname of the authenticated user
   * @param request         the DTO containing new profile data
   * @throws IllegalArgumentException if the user is not found or the new nickname is already in use
   */
  @Transactional
  public void updateProfile(String currentUsername, ProfileDto.UpdateProfileRequest request) {
    User user = userRepository.findByUsername(currentUsername)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    // Nickname update logic
    if (request.getNewUsername() != null && !request.getNewUsername().isEmpty() && !request.getNewUsername().equals(user.getUsername())) {
      if (userRepository.findByUsername(request.getNewUsername()).isPresent()) {
        throw new IllegalArgumentException("This username is already taken");
      }
      user.setUsername(request.getNewUsername());
    }

    // Biography update logic
    if (request.getBio() != null) {
      user.setBio(request.getBio());
    }

    // Birth date update logic
    if (request.getBirthDate() != null) {
      user.setBirthDate(request.getBirthDate());
    }

    // Secure password update logic
    if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
      user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    userRepository.save(user);
  }
}