package com.example.movie.service.cinema;

import com.example.movie.domain.cinema.*;
import com.example.movie.domain.movie.MovieRepository;
import com.example.movie.dto.ShowtimeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.*;

@Service
public class ShowtimeService {
    private final ShowtimeRepository repo;
    private final MovieRepository movieRepo;
    private final CinemaRepository cinemaRepo;

    public ShowtimeService(ShowtimeRepository repo, MovieRepository movieRepo, CinemaRepository cinemaRepo) {
        this.repo = repo; this.movieRepo = movieRepo; this.cinemaRepo = cinemaRepo;
    }

    @Transactional
    public ShowtimeDTO create(ShowtimeDTO dto){
        var movie  = movieRepo.findById(dto.movieId()).orElseThrow(() -> new IllegalArgumentException("Movie not found"));
        var cinema = cinemaRepo.findById(dto.cinemaId()).orElseThrow(() -> new IllegalArgumentException("Cinema not found"));

        var start = dto.startTime(); var end = dto.endTime();
        if (start == null || end == null || !end.isAfter(start)) throw new IllegalArgumentException("Invalid time range");
        BigDecimal price = dto.price() == null ? new BigDecimal("90000.00") : dto.price();

        var s = new Showtime();
        s.setMovie(movie); s.setCinema(cinema);
        s.setStartTime(start); s.setEndTime(end);
        s.setPrice(price); s.setStatus(dto.status() == null ? "OPEN" : dto.status());
        s = repo.save(s);
        return toDTO(s, ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime());
    }

    /**
     * state = NOW_SHOWING | UPCOMING | (null = tất cả)
     * date (yyyy-MM-dd) tùy chọn: lọc theo ngày (dải [00:00, 23:59:59])
     */
    @Transactional(readOnly = true)
    public Page<ShowtimeDTO> search(Long movieId, Long cinemaId, String state, LocalDate date, Pageable pageable) {
        LocalDateTime now = ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime from = null, to = null;
        if (date != null) {
            from = date.atStartOfDay();
            to   = date.atTime(LocalTime.MAX);
        } else if ("UPCOMING".equalsIgnoreCase(state)) {
            from = now; // chỉ lấy các suất bắt đầu sau hiện tại
        }

        Page<Showtime> page = repo.search(movieId, cinemaId, from, to, pageable);
        Page<ShowtimeDTO> mapped = page.map(s -> toDTO(s, now));

        if (state == null || state.isBlank()) {
            return mapped; // không lọc thêm
        }

        String want = state.toUpperCase();
        List<ShowtimeDTO> filtered = mapped.getContent().stream()
                .filter(dto -> want.equals(dto.computedState()))
                .collect(Collectors.toList());

        return new PageImpl<>(filtered, pageable, filtered.size());
    }


    private ShowtimeDTO toDTO(Showtime s, LocalDateTime now) {
        String computed = computeState(s.getStartTime(), s.getEndTime(), now);
        return new ShowtimeDTO(
                s.getId(),
                s.getMovie().getId(),
                s.getMovie().getTitle(),
                s.getCinema().getId(),
                s.getCinema().getName(),
                s.getStartTime(),
                s.getEndTime(),
                s.getPrice(),
                s.getStatus(),
                computed
        );
    }

    private String computeState(LocalDateTime start, LocalDateTime end, LocalDateTime now){
        if ((now.isAfter(start) || now.isEqual(start)) && now.isBefore(end)) return "NOW_SHOWING";
        if (now.isBefore(start)) return "UPCOMING";
        return "ENDED";
    }
}
