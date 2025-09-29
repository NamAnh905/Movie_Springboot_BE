package com.example.movie.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String username,
        String fullName,
        String email,
        String status,
        String role,
        Boolean enabled,
        Instant createdAt
) {}
