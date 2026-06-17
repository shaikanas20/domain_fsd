package com.cinereserve.auth.dto;

import com.cinereserve.auth.model.Role;
import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String name,
    String email,
    Role role,
    String phone,
    LocalDateTime createdAt
) {}
