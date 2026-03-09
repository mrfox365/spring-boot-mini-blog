package com.example.project.controller;

import com.example.project.dto.EmailRequest;
import com.example.project.dto.LoginRequest;
import com.example.project.dto.RegisterRequest;
import com.example.project.dto.ResetPasswordRequest;
import com.example.project.dto.VerifyRequest;
import com.example.project.service.AuthService;
import com.example.project.security.JwtService;
import com.example.project.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling authentication and registration operations.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @Autowired
  private JwtService jwtService;

  /**
   * Sends a registration verification code to the user's email.
   *
   * @param request the registration details
   * @return a response indicating the result of the operation
   */
  @PostMapping("/send-code")
  public ResponseEntity<String> sendCode(@RequestBody RegisterRequest request) {
    try {
      authService.sendRegistrationCode(request.getEmail());
      return ResponseEntity.ok("Verification code sent successfully");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Completes the user registration process using a verification code.
   *
   * @param request the verification data including code and credentials
   * @return a success or error response
   */
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody VerifyRequest request) {
    try {
      authService.completeRegistration(
          request.getUsername(),
          request.getEmail(),
          request.getPassword(),
          request.getCode()
      );
      return ResponseEntity.ok("Registration successful");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Authenticates a user and sets a secure HttpOnly JWT cookie upon success.
   *
   * @param request the login credentials
   * @return a response containing user info and a Set-Cookie header
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    User user = authService.authenticate(request.getLogin(), request.getPassword());

    if (user != null) {
      String token = jwtService.generateToken(user.getUsername());

      ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", token)
          .httpOnly(true)
          .secure(true)
          .path("/")
          .maxAge(24 * 60 * 60)
          .sameSite("Strict")
          .build();

      Map<String, String> responseBody = new HashMap<>();
      responseBody.put("username", user.getUsername());

      return ResponseEntity.ok()
          .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
          .body(responseBody);
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login or password");
    }
  }

  /**
   * Logs out the user by clearing the secure JWT cookie.
   *
   * @return a success response with an expired cookie header
   */
  @PostMapping("/logout")
  public ResponseEntity<?> logout() {
    ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", "")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0)
        .sameSite("Strict")
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body("Logged out successfully");
  }

  /**
   * Initiates the password reset process by sending a link to the email.
   *
   * @param request the request containing user's email
   * @return a response message
   */
  @PostMapping("/forgot-password")
  public ResponseEntity<String> forgotPassword(@RequestBody EmailRequest request) {
    authService.initiatePasswordReset(request.getEmail());
    return ResponseEntity.ok("If the account exists, a reset link has been sent to the email provided");
  }

  /**
   * Resets the user password using a security token.
   *
   * @param request the password reset data including token and new password
   * @return a response indicating the result
   */
  @PostMapping("/reset-password")
  public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
    try {
      authService.resetPassword(request.getToken(), request.getNewPassword());
      return ResponseEntity.ok("Password updated successfully");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}