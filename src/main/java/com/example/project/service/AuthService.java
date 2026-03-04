package com.example.project.service;

import com.example.project.entity.User;
import com.example.project.entity.VerificationCode;
import com.example.project.repository.UserRepository;
import com.example.project.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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

    // Криптографічно стійкий генератор для 6-значних кодів
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * КРОК 1: Відправка коду на пошту (Registration)
     */
    @Transactional
    public void sendRegistrationCode(String email) {
        // Перевіряємо, чи вже є такий користувач
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Користувач з таким email вже існує");
        }

        // Видаляємо старі коди для цього email, якщо вони були
        codeRepository.deleteByEmailAndType(email, VerificationCode.CodeType.REGISTRATION);

        // Генеруємо 6-значний код
        String code = String.format("%06d", secureRandom.nextInt(1000000));

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCode.setType(VerificationCode.CodeType.REGISTRATION);
        verificationCode.setExpiryDate(LocalDateTime.now().plusMinutes(15)); // Дійсний 15 хв

        codeRepository.save(verificationCode);
        emailService.sendVerificationCode(email, code);
    }

    /**
     * КРОК 2: Завершення реєстрації (перевірка коду + збереження юзера)
     */
    @Transactional
    public void completeRegistration(String username, String email, String password, String code) {
        // Шукаємо код в БД
        VerificationCode verificationCode = codeRepository
                .findByEmailAndCodeAndType(email, code, VerificationCode.CodeType.REGISTRATION)
                .orElseThrow(() -> new IllegalArgumentException("Невірний код підтвердження"));

        if (verificationCode.isExpired()) {
            throw new IllegalArgumentException("Час дії коду вичерпано. Згенеруйте новий.");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Цей нікнейм вже зайнятий");
        }

        // Створюємо користувача. Пароль ОБОВ'ЯЗКОВО хешуємо!
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmailVerified(true);

        userRepository.save(user);

        // Видаляємо використаний код
        codeRepository.deleteByEmailAndType(email, VerificationCode.CodeType.REGISTRATION);
    }

    /**
     * КРОК 3: Забули пароль (Відправка посилання)
     */
    @Transactional
    public void initiatePasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        // ЗАХИСТ ВІД ENUMERATION: Нічого не робимо, якщо юзера немає, але не викидаємо помилку
        if (userOpt.isEmpty()) {
            return;
        }

        codeRepository.deleteByEmailAndType(email, VerificationCode.CodeType.PASSWORD_RESET);

        // Генеруємо складний унікальний токен
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
     * КРОК 4: Встановлення нового пароля за токеном
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // У цьому випадку ми шукаємо запис суто за унікальним токеном
        VerificationCode verificationCode = codeRepository.findAll().stream()
                .filter(c -> c.getCode().equals(token) && c.getType() == VerificationCode.CodeType.PASSWORD_RESET)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Недійсний або використаний токен"));

        if (verificationCode.isExpired()) {
            throw new IllegalArgumentException("Час дії посилання вичерпано");
        }

        User user = userRepository.findByEmail(verificationCode.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено"));

        // Хешуємо новий пароль
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Видаляємо токен
        codeRepository.deleteByEmailAndType(verificationCode.getEmail(), VerificationCode.CodeType.PASSWORD_RESET);
    }

    /**
     * КРОК 5: Логін (за нікнеймом АБО email)
     */
    public boolean authenticate(String login, String rawPassword) {
        // Шукаємо користувача спочатку за нікнеймом, а якщо немає - за email
        Optional<User> userOpt = userRepository.findByUsername(login);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(login);
        }

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Порівнюємо введений пароль із захешованим у базі
            return passwordEncoder.matches(rawPassword, user.getPassword());
        }

        return false; // Користувача не знайдено або пароль невірний
    }
}