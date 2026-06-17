package com.cinereserve.auth;

import com.cinereserve.auth.dto.RegisterRequest;
import com.cinereserve.auth.dto.UserResponse;
import com.cinereserve.auth.exception.AuthException;
import com.cinereserve.auth.model.Role;
import com.cinereserve.auth.model.User;
import com.cinereserve.auth.repository.RefreshTokenRepository;
import com.cinereserve.auth.repository.UserRepository;
import com.cinereserve.auth.security.JwtUtils;
import com.cinereserve.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password", Role.USER, "1234567890");
        
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        
        User savedUser = User.builder()
                .id(1L)
                .name(request.name())
                .email(request.email())
                .password("encodedPassword")
                .role(request.role())
                .phone(request.phone())
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("John Doe", response.name());
        assertEquals("john@example.com", response.email());
        assertEquals(Role.USER, response.role());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password", Role.USER, "1234567890");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(AuthException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetProfile_Success() {
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .role(Role.USER)
                .phone("1234567890")
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        UserResponse response = authService.getProfile("john@example.com");

        assertNotNull(response);
        assertEquals("john@example.com", response.email());
        assertEquals("John Doe", response.name());
    }

    @Test
    void testGetProfile_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> authService.getProfile("nonexistent@example.com"));
    }
}
