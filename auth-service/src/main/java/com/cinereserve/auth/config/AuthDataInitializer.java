package com.cinereserve.auth.config;

import com.cinereserve.auth.model.Role;
import com.cinereserve.auth.model.User;
import com.cinereserve.auth.repository.RefreshTokenRepository;
import com.cinereserve.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds / resets default user accounts on every startup.
 * This ensures known credentials are always available regardless
 * of how the database was previously populated.
 */
@Component
public class AuthDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthDataInitializer(UserRepository userRepository,
                               RefreshTokenRepository refreshTokenRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        upsertUser("Admin User",    "admin@cinereserve.com",  "admin123",  "9000000001", Role.ADMIN);
        upsertUser("Test Customer", "user@cinereserve.com",   "user123",   "9000000002", Role.USER);
        upsertUser("Theatre Owner", "owner@cinereserve.com",  "owner123",  "9000000003", Role.THEATRE_OWNER);
        System.out.println(">>> Auth: default user accounts ensured (admin / user / owner).");
    }

    private void upsertUser(String name, String email, String rawPassword, String phone, Role role) {
        String encoded = passwordEncoder.encode(rawPassword);
        userRepository.findByEmail(email).ifPresentOrElse(
            existing -> {
                // Always reset to the known password so credentials are predictable
                existing.setPassword(encoded);
                existing.setRole(role);
                userRepository.save(existing);
            },
            () -> userRepository.save(User.builder()
                    .name(name)
                    .email(email)
                    .password(encoded)
                    .role(role)
                    .phone(phone)
                    .build())
        );
    }
}
