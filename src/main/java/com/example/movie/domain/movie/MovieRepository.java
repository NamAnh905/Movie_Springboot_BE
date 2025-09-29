package com.example.movie.domain.movie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Lọc theo genreId (join movie_genres) + q theo title (nullable)
    @Query(value = """
        SELECT m.* FROM movies m
        JOIN movie_genres mg ON m.id = mg.movie_id
        WHERE (:genreId IS NULL OR mg.genre_id = :genreId)
          AND (:q IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%')))
        GROUP BY m.id
        """,
            countQuery = """
        SELECT COUNT(DISTINCT m.id) FROM movies m
        JOIN movie_genres mg ON m.id = mg.movie_id
        WHERE (:genreId IS NULL OR mg.genre_id = :genreId)
          AND (:q IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%')))
        """,
            nativeQuery = true)
    Page<Movie> searchByGenreAndTitle(@Param("genreId") Long genreId,
                                      @Param("q") String q,
                                      Pageable pageable);
}
