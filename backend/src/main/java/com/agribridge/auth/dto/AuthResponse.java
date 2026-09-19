package com.agribridge.auth.dto;

import com.agribridge.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Returned on both successful registration and login. The token is a signed
 * JWT (JwtUtil) issued as a forward-compatible foundation; note that
 * enforcement of this token on protected endpoints is planned for Week 3 -
 * Week 2 endpoints are protected via HTTP Basic using the same credentials.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long userId;
    private String name;
    private String email;
    private Role role;
    private String token;
    private long expiresInSeconds;
}
