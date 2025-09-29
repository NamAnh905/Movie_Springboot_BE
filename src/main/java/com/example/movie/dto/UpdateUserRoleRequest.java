package com.example.movie.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRoleRequest(
        @NotBlank String role // ADMIN | USER
) {}
