package com.example.project.controller;

import com.example.project.dto.ProfileDto.UpdateProfileRequest;
import com.example.project.dto.ProfileDto.UserProfileResponse;
import com.example.project.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling user profile operations.
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

  @Autowired
  private ProfileService profileService;

  /**
   * Retrieves the profile data for a specific user.
   *
   * @param username the target username
   * @return the user profile response or an error message
   */
  @GetMapping("/{username}")
  public ResponseEntity<?> getProfile(@PathVariable String username) {
    try {
      return ResponseEntity.ok(profileService.getProfile(username));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Updates the profile data for the authenticated user.
   *
   * @param request the update profile request
   * @return a success response or an error message
   */
  @PutMapping("/update")
  public ResponseEntity<String> updateProfile(@RequestBody UpdateProfileRequest request) {
    try {
      profileService.updateProfile(request);
      return ResponseEntity.ok("Profile updated successfully");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}