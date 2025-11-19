package com.team7.ConcerTUNE.temp.service;

import com.team7.ConcerTUNE.entity.LiveArtist;
import com.team7.ConcerTUNE.entity.Lives;
import com.team7.ConcerTUNE.repository.LiveArtistRepository;
import com.team7.ConcerTUNE.service.AuthService;
import com.team7.ConcerTUNE.temp.dto.FollowArtistFeedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowFeedService {

    private final LiveArtistRepository liveArtistRepository;
    private final AuthService authService;

    public List<FollowArtistFeedDto> getFollowArtistFeeds(Authentication authentication) {
        Long userId = authService.getUserFromAuth(authentication).getId();
        List<LiveArtist> liveArtists = liveArtistRepository.findUpcomingLivesByUser(userId, LocalDate.now());

        return liveArtists.stream()
                .map(la -> {
                    Lives live = la.getLive();
                    LocalDate earliestDate = live.getLiveSchedules().stream()
                            .map(ls -> ls.getSchedule().getLiveDate())
                            .min(LocalDate::compareTo)
                            .orElse(LocalDate.now());
                    return new FollowArtistFeedDto(
                            live.getId(),
                            live.getTitle(),
                            la.getArtist().getArtistName(),
                            earliestDate,
                            live.getPosterUrl()
                    );
                })
                .sorted(Comparator.comparing(FollowArtistFeedDto::getScheduleDate))
                .limit(5)
                .toList();
    }
}