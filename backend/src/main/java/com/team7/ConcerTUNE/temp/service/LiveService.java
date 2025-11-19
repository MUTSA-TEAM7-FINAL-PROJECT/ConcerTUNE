package com.team7.ConcerTUNE.temp.service;

import com.team7.ConcerTUNE.entity.Lives;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.repository.FollowRepository;
import com.team7.ConcerTUNE.repository.LiveArtistRepository;
import com.team7.ConcerTUNE.service.AuthService;
import com.team7.ConcerTUNE.temp.dto.*;
import com.team7.ConcerTUNE.temp.repository.BookmarkRepository;
import com.team7.ConcerTUNE.temp.repository.LiveRepository;
import com.team7.ConcerTUNE.temp.repository.UserGenrePreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LiveService {

    private final LiveRepository liveRepository;
    private final UserGenrePreferenceRepository userGenrePreferenceRepository;
    private final AuthService authService;

    public Page<LiveResponse> findAllLives(String genre, Pageable pageable) {
        Page<Lives> livesPage;

        if (genre != null && !genre.trim().isEmpty() && !genre.equalsIgnoreCase("전체")) {
            livesPage = liveRepository.findLivesByGenreName(genre, pageable);
        } else {
            livesPage = liveRepository.findAll(pageable);
        }

        return livesPage.map(LiveResponse::fromEntity);
    }

    public LiveResponse findLiveById(Long liveId) {
        Lives live = liveRepository.findById(liveId)
                .orElseThrow(() -> new IllegalArgumentException("Live not found with id: " + liveId));
        return LiveResponse.fromEntity(live);
    }

    public List<LiveSummaryDto> getLatest4Concerts() {
        PageRequest pageRequest = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "createdAt"));

        return liveRepository.findTop4ByOrderByCreatedAtDesc(pageRequest).stream()
                .map(LiveSummaryDto::new)
                .collect(Collectors.toList());
    }

    public List<PersonalizedLiveDto> getPersonalizedConcerts(Authentication authentication) {

        User user = authService.getUserFromAuth(authentication);
        List<Long> preferredGenreIds = userGenrePreferenceRepository.findGenreIdsByUserId(user.getId());

        if (preferredGenreIds.isEmpty()) {
            return List.of();
        }

        // Pageable 객체 생성: 상위 8개 제한
        Pageable pageable = PageRequest.of(0, 8);

        // 단일 쿼리 호출: N+1 문제 해결
        List<Lives> recommendedLives = liveRepository.findPersonalizedRecommendations(
                preferredGenreIds,
                LocalDate.now(),
                pageable
        );

        // DTO 변환
        return recommendedLives.stream()
                .map(PersonalizedLiveDto::new)
                .collect(Collectors.toList());
    }

}