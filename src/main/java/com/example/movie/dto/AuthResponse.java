package com.example.movie.dto;

public record AuthResponse(String accessToken, String tokenType) { public AuthResponse(String t){ this(t,"Bearer"); } }