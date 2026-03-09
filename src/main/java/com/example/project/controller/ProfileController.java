package com.example.project.controller;

import com.example.project.dto.ProfileDto.UserProfileResponse;
import com.example.project.dto.ProfileDto.UpdateProfileRequest;
import com.example.project.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controller for handling user profile-related operations.
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

  @Autowired
  private ProfileService profileService;

  /**
   * Retrieves the public profile information of a user.
   *
   * @param username the username of the profile to retrieve
   * @return a response containing the user profile data or a 404 status if not found
   */
  @GetMapping("/{username}")
  public ResponseEntity<UserProfileResponse> getProfile(@PathVariable String username) {
    try {
      UserProfileResponse profile = profileService.getProfile(username);
      return ResponseEntity.ok(profile);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Updates the profile information for the currently authenticated user.
   *
   * @param request   the new profile data
   * @param principal the security context of the authenticated user
   * @return a success message or an error response
   */
  @PutMapping("/update")
  public ResponseEntity<String> updateProfile(@RequestBody UpdateProfileRequest request, Principal principal) {
    if (principal == null) {
      return ResponseEntity.status(401).body("Unauthorized");
    }
    try {
      // The current username is taken directly from the secure Principal context
      profileService.updateProfile(principal.getName(), request);
      return ResponseEntity.ok("Profile updated successfully");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}