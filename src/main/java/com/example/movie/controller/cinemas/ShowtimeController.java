package com.example.movie.controller.cinemas;

import com.example.movie.dto.ShowtimeDTO;
import com.example.movie.service.cinema.ShowtimeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeController {
    private final ShowtimeService service;
    public ShowtimeController(ShowtimeService service){ this.service = service; }

    // Tạo lịch chiếu
    @PostMapping
    public ShowtimeDTO create(@RequestBody ShowtimeDTO dto){ return service.create(dto); }

    // Tìm kiếm lịch chiếu theo trạng thái "NOW_SHOWING" | "UPCOMING" (tùy chọn),
    // theo ngày (yyyy-MM-dd, tùy chọn), movieId/cinemaId (tùy chọn)
    @GetMapping
    public Page<ShowtimeDTO> search(
            @RequestParam(required=false) Long movieId,
            @RequestParam(required=false) Long cinemaId,
            @RequestParam(required=false) String state,
            @RequestParam(required=false) String date, // yyyy-MM-dd
            Pageable pageable){
        LocalDate d = (date!=null && !date.isBlank()) ? LocalDate.parse(date) : null;
        return service.search(movieId, cinemaId, state, d, pageable);
    }
}
