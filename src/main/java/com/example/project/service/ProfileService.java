package com.example.project.service;

import com.example.project.dto.ProfileDto.UpdateProfileRequest;
import com.example.project.dto.ProfileDto.UserProfileResponse;
import com.example.project.entity.User;
import com.example.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing user profiles.
 */
@Service
public class ProfileService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * Retrieves user profile details by username.
   *
   * @param username the username of the user
   * @return the constructed user profile response
   */
  public UserProfileResponse getProfile(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    UserProfileResponse response = new UserProfileResponse();
    response.setUsername(user.getUsername());
    response.setEmail(user.getEmail());
    response.setBio(user.getBio());
    response.setBirthDate(user.getBirthDate());
    return response;
  }

  /**
   * Updates the profile information for a user.
   *
   * @param request the request containing updated profile data
   */
  @Transactional
  public void updateProfile(UpdateProfileRequest request) {
    User user = userRepository.findByUsername(request.getCurrentUsername())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (request.getNewUsername() != null
        && !request.getNewUsername().isEmpty()
        && !request.getNewUsername().equals(user.getUsername())) {
      if (userRepository.findByUsername(request.getNewUsername()).isPresent()) {
        throw new IllegalArgumentException("Username is already taken");
      }
      user.setUsername(request.getNewUsername());
    }

    if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
      user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    user.setBio(request.getBio());
    user.setBirthDate(request.getBirthDate());

    userRepository.save(user);
  }
}