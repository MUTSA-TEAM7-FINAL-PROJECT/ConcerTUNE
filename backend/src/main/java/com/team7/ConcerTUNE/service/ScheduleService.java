package com.team7.ConcerTUNE.temp.service;

import com.team7.ConcerTUNE.entity.LiveArtist;
import com.team7.ConcerTUNE.entity.Lives;
import com.team7.ConcerTUNE.repository.LiveArtistRepository;
import com.team7.ConcerTUNE.repository.UserArtistRepository;
import com.team7.ConcerTUNE.service.AuthService;
import com.team7.ConcerTUNE.dto.LiveScheduleResponseDto;
import com.team7.ConcerTUNE.repository.BookmarkRepository;
import com.team7.ConcerTUNE.repository.LiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final AuthService authService;
    private final UserArtistRepository userArtistRepository;
    private final LiveArtistRepository liveArtistRepository;
    private final BookmarkRepository bookmarkRepository;
    private final LiveRepository livesRepository;

    public List<LiveScheduleResponseDto> getSchedulesByArtistId(Long artistId) {

        List<LiveArtist> liveArtists = liveArtistRepository.findByArtist_ArtistId(artistId);

        List<Lives> lives = liveArtists.stream()
                .map(LiveArtist::getLive)
                .distinct()
                .collect(Collectors.toList());

        List<LiveScheduleResponseDto> schedulesDto = lives.stream()
                .flatMap(live -> live.getLiveSchedules().stream()) // Live의 모든 LiveSchedules를 평탄화
                .map(LiveScheduleResponseDto::of)
                .sorted(Comparator.comparing(LiveScheduleResponseDto::getLiveDate).thenComparing(LiveScheduleResponseDto::getLiveTime))
                .collect(Collectors.toList());

        return schedulesDto;
    }

    public List<LiveScheduleResponseDto> getPersonalizedUpcomingLives(Long userId) {
        LocalDate today = LocalDate.now();

        List<Long> followedArtistIds = userArtistRepository.findByUserId(userId).stream()
                .map(ua -> ua.getArtist().getArtistId())
                .collect(Collectors.toList());

        Stream<Lives> followedLivesStream = followedArtistIds.isEmpty() ?
                Stream.empty() :
                liveArtistRepository.findByArtist_ArtistIdIn(followedArtistIds).stream()
                        .map(LiveArtist::getLive);


        List<Long> bookmarkedLiveIds = bookmarkRepository.findLiveIdsByUserId(userId);

        Stream<Lives> bookmarkedLivesStream = bookmarkedLiveIds.isEmpty() ?
                Stream.empty() :
                livesRepository.findAllById(bookmarkedLiveIds).stream();


        Set<Lives> combinedLives = Stream.concat(followedLivesStream, bookmarkedLivesStream)
                .distinct()
                .collect(Collectors.toSet());

        List<LiveScheduleResponseDto> schedulesDto = combinedLives.stream()
                .flatMap(live -> live.getLiveSchedules().stream())
                .filter(ls -> ls.getSchedule().getLiveDate().isEqual(today) || ls.getSchedule().getLiveDate().isAfter(today))
                .map(LiveScheduleResponseDto::of)
                .sorted(Comparator.comparing(LiveScheduleResponseDto::getLiveDate)
                        .thenComparing(LiveScheduleResponseDto::getLiveTime, Comparator.nullsFirst(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        return schedulesDto;
    }
}