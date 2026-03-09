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
 * Service class for managing authentication, registration, and password recovery.
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

  private final SecureRandom secureRandom = new SecureRandom();

  /**
   * Generates and sends a registration verification code to the user's email.
   *
   * @param email the email address to send the code to
   * @throws IllegalArgumentException if a user with the given email already exists
   */
  @Transactional
  public void sendRegistrationCode(String email) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new IllegalArgumentException("User with this email already exists");
    }
    codeRepository.deleteByEmailAndType(email, VerificationCode.CodeType.REGISTRATION);
    String code = String.format("%06d", secureRandom.nextInt(1000000));

    VerificationCode verificationCode = new VerificationCode();
    verificationCode.setEmail(email);
    verificationCode.setCode(code);
    verificationCode.setType(VerificationCode.CodeType.REGISTRATION);
    verificationCode.setExpiryDate(LocalDateTime.now().plusMinutes(15));

    codeRepository.save(verificationCode);
    emailService.sendVerificationCode(email, code);
  }

  /**
   * Completes the user registration process after code verification.
   *
   * @param username the user's chosen nickname
   * @param email    the user's verified email address
   * @param password the user's password
   * @param code     the verification code provided by the user
   * @throws IllegalArgumentException if the code is invalid, expired, or the username is taken
   */
  @Transactional
  public void completeRegistration(String username, String email, String password, String code) {
    VerificationCode verificationCode = codeRepository
        .findByEmailAndCodeAndType(email, code, VerificationCode.CodeType.REGISTRATION)
        .orElseThrow(() -> new IllegalArgumentException("Invalid code"));

    if (verificationCode.isExpired()) {
      throw new IllegalArgumentException("Code expired");
    }
    if (userRepository.findByUsername(username).isPresent()) {
      throw new IllegalArgumentException("Username taken");
    }

    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    user.setEmailVerified(true);

    userRepository.save(user);
    codeRepository.deleteByEmailAndType(email, VerificationCode.CodeType.REGISTRATION);
  }

  /**
   * Initiates the password reset process by generating a security token.
   *
   * @param email the email address of the account to recover
   */
  @Transactional
  public void initiatePasswordReset(String email) {
    Optional<User> userOpt = userRepository.findByEmail(email);
    if (userOpt.isEmpty()) return;

    codeRepository.deleteByEmailAndType(email, VerificationCode.CodeType.PASSWORD_RESET);
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
   * Resets the user's password using a valid security token.
   *
   * @param token       the recovery token
   * @param newPassword the new password to be set
   * @throws IllegalArgumentException if the token is invalid, expired, or the user is not found
   */
  @Transactional
  public void resetPassword(String token, String newPassword) {
    VerificationCode verificationCode = codeRepository.findAll().stream()
        .filter(c -> c.getCode().equals(token) && c.getType() == VerificationCode.CodeType.PASSWORD_RESET)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

    if (verificationCode.isExpired()) {
      throw new IllegalArgumentException("Link expired");
    }

    User user = userRepository.findByEmail(verificationCode.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
    codeRepository.deleteByEmailAndType(verificationCode.getEmail(), VerificationCode.CodeType.PASSWORD_RESET);
  }

  /**
   * Authenticates a user based on their login credentials.
   *
   * @param login       the username or email address
   * @param rawPassword the unencoded password
   * @return the authenticated User object or null if credentials are invalid
   */
  public User authenticate(String login, String rawPassword) {
    Optional<User> userOpt = userRepository.findByUsername(login);
    if (userOpt.isEmpty()) {
      userOpt = userRepository.findByEmail(login);
    }

    if (userOpt.isPresent()) {
      User user = userOpt.get();
      if (passwordEncoder.matches(rawPassword, user.getPassword())) {
        return user;
      }
    }
    return null;
  }
}