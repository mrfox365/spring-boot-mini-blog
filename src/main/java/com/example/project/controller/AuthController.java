package com.example.project.controller;

import com.example.project.dto.EmailRequest;
import com.example.project.dto.LoginRequest;
import com.example.project.dto.RegisterRequest;
import com.example.project.dto.ResetPasswordRequest;
import com.example.project.dto.VerifyRequest;
import com.example.project.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling authentication and registration operations.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  /**
   * Sends a registration verification code to the provided email.
   *
   * @param request the registration request containing user details
   * @return a success or error response entity
   */
  @PostMapping("/send-code")
  public ResponseEntity<String> sendCode(@RequestBody RegisterRequest request) {
    try {
      authService.sendRegistrationCode(request.getEmail());
      return ResponseEntity.ok("Код успішно відправлено на пошту");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Помилка відправки листа");
    }
  }

  /**
   * Verifies the confirmation code and saves the new user to the database.
   *
   * @param request the verification request
   * @return a success or error response entity
   */
  @PostMapping("/verify-and-save")
  public ResponseEntity<String> verifyAndSave(@RequestBody VerifyRequest request) {
    try {
      authService.completeRegistration(
          request.getUsername(),
          request.getEmail(),
          request.getPassword(),
          request.getCode()
      );
      return ResponseEntity.ok("Реєстрація успішно завершена");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Authenticates a user based on their login (username or email) and password.
   *
   * @param request the login request
   * @return a success or error response entity
   */
  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest request) {
    boolean isAuthenticated = authService.authenticate(request.getLogin(), request.getPassword());

    if (isAuthenticated) {
      // In a full-fledged application, a JWT token is usually generated and returned here
      return ResponseEntity.ok("Вхід успішний!");
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невірний логін або пароль");
    }
  }

  /**
   * Initiates the password reset process by sending an email link.
   *
   * @param request the email request
   * @return a success message
   */
  @PostMapping("/forgot-password")
  public ResponseEntity<String> forgotPassword(@RequestBody EmailRequest request) {
    // We always return OK to avoid revealing if the email exists (protection against Enumeration)
    authService.initiatePasswordReset(request.getEmail());
    return ResponseEntity.ok(
        "Якщо вказаний email існує, на нього відправлено посилання для відновлення"
    );
  }

  /**
   * Resets the user's password using the provided security token.
   *
   * @param request the reset password request containing the token and new password
   * @return a success or error response entity
   */
  @PostMapping("/reset-password")
  public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
    try {
      authService.resetPassword(request.getToken(), request.getNewPassword());
      return ResponseEntity.ok("Пароль успішно змінено");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}