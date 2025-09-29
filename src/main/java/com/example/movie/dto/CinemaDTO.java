// com.example.movie.dto.CinemaDTO.java
package com.example.movie.dto;

public record CinemaDTO(
        Long id,
        String name,
        String address,
        String status
) {}
