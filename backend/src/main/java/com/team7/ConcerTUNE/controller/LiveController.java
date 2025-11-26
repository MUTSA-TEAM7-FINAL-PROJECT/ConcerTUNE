package com.team7.ConcerTUNE.temp.controller;

import com.team7.ConcerTUNE.temp.dto.*;
import com.team7.ConcerTUNE.temp.service.LiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lives")
public class LiveController {

    private final LiveService liveService;

    /**
     * GET /api/lives (ALL)
     */
    @GetMapping
    public ResponseEntity<Page<LiveResponse>> getAllLives(
            @RequestParam(required = false) String genre,
            Pageable pageable) {

        Page<LiveResponse> livesPage = liveService.findAllLives(genre, pageable);
        return ResponseEntity.ok(livesPage);
    }

    /**
     * GET /api/lives/{liveId} (ALL)
     */
    @GetMapping("/{liveId}")
    public ResponseEntity<LiveResponse> getLiveById(@PathVariable Long liveId) {
        return ResponseEntity.ok(liveService.findLiveById(liveId));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<LiveSummaryDto>> getLatest4Concerts() {
        List<LiveSummaryDto> latestConcerts = liveService.getLatest4Concerts();
        return ResponseEntity.ok(latestConcerts);
    }


    @GetMapping("/personalized")
    public ResponseEntity<List<PersonalizedLiveDto>> getPersonalizedConcerts(
            Authentication authentication
    ) {
        List<PersonalizedLiveDto> recommendedConcerts = liveService.getPersonalizedConcerts(authentication);
        return ResponseEntity.ok(recommendedConcerts);
    }
}