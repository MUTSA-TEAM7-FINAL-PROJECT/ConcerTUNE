package com.team7.ConcerTUNE.service;

import com.team7.ConcerTUNE.dto.ArtistSummaryDto;
import com.team7.ConcerTUNE.dto.LiveResponse;
import com.team7.ConcerTUNE.dto.LiveSummaryResponse;
import com.team7.ConcerTUNE.entity.*;
import com.team7.ConcerTUNE.exception.ResourceNotFoundException;
import com.team7.ConcerTUNE.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LiveService {
    private final LiveRepository liveRepository;
    private final BookmarkRepository bookmarkRepository;
    private final LiveArtistRepository liveArtistRepository;
    private final LiveScheduleRepository liveScheduleRepository;
    private final BookmarkService bookmarkService;

    // 공연 전체 조회
    public Page<LiveSummaryResponse> getAllLives(Pageable pageable, User user) {
        Page<Live> lives = liveRepository.findAllByRequestStatus(RequestStatus.APPROVED, pageable);

        return lives.map(live -> {
            LiveSummaryResponse response = LiveSummaryResponse.fromEntity(live);

            boolean isBookmarked = bookmarkService.isBookmarked(live.getId(), user);
            response.setIsBookmarked(isBookmarked);

            return response;
        });
    }

    // 공연 단일 조회
    public LiveResponse getLive(Long liveId, User user) {
        Live live = liveRepository.findByIdAndRequestStatus(liveId, RequestStatus.APPROVED)
                .orElseThrow(() -> new ResourceNotFoundException("공연을 찾을 수 없습니다. ID: " + liveId));

        LiveResponse response = LiveResponse.fromEntity(live);

        boolean isBookmarked = bookmarkService.isBookmarked(live.getId(), user);
        response.setIsBookmarked(isBookmarked);

        return response;
    }

    // 아티스트 조회
    public List<ArtistSummaryDto> getArtists(Long liveId) {
        List<LiveArtist> liveArtists = liveArtistRepository.findAllByLiveId(liveId);

        return liveArtists.stream()
                .map(LiveArtist::getArtist)
                .map(ArtistSummaryDto::fromEntity)
                .toList();
    }

//    // 어제 공연 1개와 오늘 이후 공연 조회
//    public Page<LiveSummaryResponse> getUpcoming(Pageable pageable) {
//
//    }

    // 가장 가까운 시일 내에 예정된 공연 n개 조회
    public List<LiveSummaryResponse> getUpcomingLives(User user, int n) {
        LocalDate today = LocalDate.now();
        PageRequest pageRequest = PageRequest.of(0, n*5);

        List<LiveSchedule> upcomingLives = liveScheduleRepository
                .findByLive_RequestStatusAndSchedule_LiveDateGreaterThanEqualOrderBySchedule_LiveDateAscSchedule_LiveTimeAsc(
                        RequestStatus.APPROVED,
                        today,
                        pageRequest
        );

        Map<Long, LiveSummaryResponse> map = new LinkedHashMap<>();

        boolean isBookmarked;
        Live live;

        for (LiveSchedule ls : upcomingLives) {
            live = ls.getLive();
            Long liveId = live.getId();

            isBookmarked = bookmarkService.isBookmarked(liveId, user);

            if (!map.containsKey(liveId)) {
                map.put(liveId, LiveSummaryResponse.fromEntity(live, isBookmarked));
            }

            if (map.size() == n) {
                break;
            }
        }

        return new ArrayList<>(map.values());
    }
}

