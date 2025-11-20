package com.team7.ConcerTUNE.temp.controller;

import com.team7.ConcerTUNE.temp.dto.LiveScheduleResponseDto;
import com.team7.ConcerTUNE.temp.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/artists/{artistId}")
    public ResponseEntity<List<LiveScheduleResponseDto>> getSchedulesByArtist(
            @PathVariable Long artistId) {
        List<LiveScheduleResponseDto> schedules = scheduleService.getSchedulesByArtistId(artistId);

        if (schedules.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/personalized")
    public ResponseEntity<List<LiveScheduleResponseDto>> getPersonalizedSchedules(
            @RequestParam(required = true) Long userId
    ) {
        List<LiveScheduleResponseDto> schedules = scheduleService.getPersonalizedUpcomingLives(userId);

        if (schedules.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(schedules);
    }
}