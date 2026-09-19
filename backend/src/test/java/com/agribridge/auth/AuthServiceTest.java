package com.agribridge.auth;

import com.agribridge.auth.dto.AuthResponse;
import com.agribridge.auth.dto.RegisterRequest;
import com.agribridge.exception.DuplicateResourceException;
import com.agribridge.security.JwtUtil;
import com.agribridge.user.Role;
import com.agribridge.user.User;
import com.agribridge.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Ravi Kumar");
        registerRequest.setEmail("ravi.kumar@example.com");
        registerRequest.setPassword("SecurePass123");
        registerRequest.setRole(Role.FARMER);
    }

    @Test
    void register_hashesPasswordAndReturnsToken() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123")).thenReturn("bcrypt-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("signed-jwt-token");
        when(jwtUtil.getExpirationSeconds()).thenReturn(3600L);

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("signed-jwt-token");
        assertThat(response.getEmail()).isEqualTo("ravi.kumar@example.com");
        assertThat(response.getRole()).isEqualTo(Role.FARMER);

        verify(passwordEncoder, times(1)).encode("SecurePass123");
        verify(userRepository, never()).save(argThat(u -> "SecurePass123".equals(u.getPasswordHash())));
    }

    @Test
    void register_rejectsDuplicateEmail() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        verify(userRepository, never()).save(any());
    }
}
