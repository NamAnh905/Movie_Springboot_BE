package com.example.movie.domain.cinema;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    boolean existsByName(String name);
}
