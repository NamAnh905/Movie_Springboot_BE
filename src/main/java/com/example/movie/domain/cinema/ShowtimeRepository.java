package com.example.movie.domain.cinema;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import java.time.LocalDateTime;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Query("""
      select s from Showtime s
      where (:movieId  is null or s.movie.id  = :movieId)
        and (:cinemaId is null or s.cinema.id = :cinemaId)
        and (:fromTime is null or s.startTime >= :fromTime)
        and (:toTime   is null or s.startTime <  :toTime)
    """)
    Page<Showtime> search(Long movieId, Long cinemaId,
                          LocalDateTime fromTime, LocalDateTime toTime,
                          Pageable pageable);
}
