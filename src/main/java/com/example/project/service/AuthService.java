package com.example.project.service;

import com.example.project.entity.User;
import com.example.project.entity.VerificationCode;
import com.example.project.repository.UserRepository;
import com.example.project.repository.VerificationCodeRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling authentication, registration, and password reset logic.
 */
@Service
public class AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private VerificationCodeRepository codeRepository;

  @Autowired
  private EmailService emailService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  // Cryptographically secure generator for 6-digit codes
  private final SecureRandom secureRandom = new SecureRandom();

  /**
   * STEP 1: Sends a registration verification code to the specified email.
   *
   * @param email the user's email address
   */
  @Transactional
  public void sendRegistrationCode(String email) {
    // Check if a user with this email already exists
    if (userRepository.findByEmail(email).isPresent()) {
      throw new IllegalArgumentException("Користувач з таким email вже існує");
    }

    // Delete old codes for this email if any exist
    codeRepository.deleteByEmailAndType(email, VerificationCode.CodeType.REGISTRATION);

    // Generate a 6-digit code
    String code = String.format("%06d", secureRandom.nextInt(1000000));

    VerificationCode verificationCode = new VerificationCode();
    verificationCode.setEmail(email);
    verificationCode.setCode(code);
    verificationCode.setType(VerificationCode.CodeType.REGISTRATION);
    verificationCode.setExpiryDate(LocalDateTime.now().plusMinutes(15)); // Valid for 15 minutes

    codeRepository.save(verificationCode);
    emailService.sendVerificationCode(email, code);
  }

  /**
   * STEP 2: Completes the registration by verifying the code and saving the user.
   *
   * @param username the user's chosen username
   * @param email the user's email address
   * @param password the user's raw password
   * @param code the verification code sent to the email
   */
  @Transactional
  public void completeRegistration(String username, String email, String password, String code) {
    // Find the code in the database
    VerificationCode verificationCode = codeRepository
        .findByEmailAndCodeAndType(email, code, VerificationCode.CodeType.REGISTRATION)
        .orElseThrow(() -> new IllegalArgumentException("Невірний код підтвердження"));

    if (verificationCode.isExpired()) {
      throw new IllegalArgumentException("Час дії коду вичерпано. Згенеруйте новий.");
    }

    if (userRepository.findByUsername(username).isPresent()) {
      throw new IllegalArgumentException("Цей нікнейм вже зайнятий");
    }

    // Create the user and explicitly hash the password
    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    user.setEmailVerified(true);

    userRepository.save(user);

    // Delete the used verification code
    codeRepository.deleteByEmailAndType(email, VerificationCode.CodeType.REGISTRATION);
  }

  /**
   * STEP 3: Initiates the password reset process by generating a token.
   *
   * @param email the user's email address
   */
  @Transactional
  public void initiatePasswordReset(String email) {
    Optional<User> userOpt = userRepository.findByEmail(email);

    // ENUMERATION PROTECTION: Do nothing if the user does not exist, but do not throw an error
    if (userOpt.isEmpty()) {
      return;
    }

    codeRepository.deleteByEmailAndType(email, VerificationCode.CodeType.PASSWORD_RESET);

    // Generate a complex unique token
    String token = UUID.randomUUID().toString();

    VerificationCode verificationCode = new VerificationCode();
    verificationCode.setEmail(email);
    verificationCode.setCode(token);
    verificationCode.setType(VerificationCode.CodeType.PASSWORD_RESET);
    verificationCode.setExpiryDate(LocalDateTime.now().plusMinutes(30));

    codeRepository.save(verificationCode);
    emailService.sendPasswordResetLink(email, token);
  }

  /**
   * STEP 4: Sets a new password using the provided token.
   *
   * @param token the password reset token
   * @param newPassword the new raw password
   */
  @Transactional
  public void resetPassword(String token, String newPassword) {
    // In this case, we search for the record solely by the unique token
    VerificationCode verificationCode = codeRepository.findAll().stream()
        .filter(c -> c.getCode().equals(token)
            && c.getType() == VerificationCode.CodeType.PASSWORD_RESET)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Недійсний або використаний токен"));

    if (verificationCode.isExpired()) {
      throw new IllegalArgumentException("Час дії посилання вичерпано");
    }

    User user = userRepository.findByEmail(verificationCode.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено"));

    // Hash the new password
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);

    // Delete the used token
    codeRepository.deleteByEmailAndType(
        verificationCode.getEmail(), VerificationCode.CodeType.PASSWORD_RESET);
  }

  /**
   * STEP 5: Authenticates a user by username OR email.
   *
   * @param login the user's username or email
   * @param rawPassword the user's raw password
   * @return true if authentication is successful, false otherwise
   */
  public boolean authenticate(String login, String rawPassword) {
    // Look for the user first by username, and if not found, search by email
    Optional<User> userOpt = userRepository.findByUsername(login);
    if (userOpt.isEmpty()) {
      userOpt = userRepository.findByEmail(login);
    }

    if (userOpt.isPresent()) {
      User user = userOpt.get();
      // Compare the entered password with the hashed password stored in the database
      return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    return false; // User not found or password incorrect
  }
}