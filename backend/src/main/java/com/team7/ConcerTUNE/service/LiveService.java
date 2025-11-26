package com.team7.ConcerTUNE.service;

import com.team7.ConcerTUNE.dto.*;
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
import java.time.LocalTime;
import java.time.YearMonth;
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
    private final UserRepository userRepository;
    private final UserArtistRepository userArtistRepository;
    private final PostRepository postRepository;

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

    public List<LiveSummaryResponse> getLivesByYearAndMonth(int year, int month, Long currentUserId) {

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        final User currentUser;
        if (currentUserId != null) {
            currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("유저를 찾을 수 없습니다. ID: " + currentUserId));
        } else {
            currentUser = null;
        }

        List<LiveSchedule> schedules =
                liveScheduleRepository
                        .findByLive_RequestStatusAndSchedule_LiveDateBetweenOrderBySchedule_LiveDateAscSchedule_LiveTimeAsc(
                                RequestStatus.APPROVED,
                                startDate,
                                endDate
                        );

        return schedules.stream()
                .map(ls -> {
                    Live live = ls.getLive();
                    Schedule schedule = ls.getSchedule();

                    boolean isBookmarked = currentUser != null &&
                            bookmarkRepository.existsByUserAndLive(currentUser, live);

                    return LiveSummaryResponse.builder()
                            .id(live.getId())
                            .title(live.getTitle())
                            .posterUrl(live.getPosterUrl())
                            .ticketUrl(live.getTicketUrl())
                            .ticketDateTime(live.getTicketDateTime())
                            .artists(
                                    live.getLiveArtists() == null ? List.of()
                                            : live.getLiveArtists().stream()
                                            .map(link -> ArtistSummaryDto.fromEntity(link.getArtist()))
                                            .toList()
                            )
                            .schedules(
                                    List.of(LiveScheduleDto.fromEntity(schedule))
                            )
                            .countBookmark(
                                    live.getBookmarks() == null ? 0 : live.getBookmarks().size()
                            )
                            .isBookmarked(isBookmarked)
                            .build();
                })
                .toList();
    }

    public LiveSummaryResponse getNearestBookmarkedLive(Long userId) {

        final User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("유저를 찾을 수 없습니다. ID: " + userId));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        Pageable pageable = PageRequest.of(0, 1); // 딱 1개만

        List<LiveSchedule> schedules =
                bookmarkRepository.findNearestFutureBookmarkedSchedule(
                        currentUser,
                        RequestStatus.APPROVED,
                        today,
                        now,
                        pageable
                );

        if (schedules.isEmpty()) {
            throw new ResourceNotFoundException("다가오는 즐겨찾기 공연이 없습니다.");
        }

        LiveSchedule nearest = schedules.get(0);
        Live live = nearest.getLive();
        Schedule schedule = nearest.getSchedule();

        return LiveSummaryResponse.builder()
                .id(live.getId())
                .title(live.getTitle())
                .posterUrl(live.getPosterUrl())
                .ticketUrl(live.getTicketUrl())
                .ticketDateTime(live.getTicketDateTime())
                .artists(
                        live.getLiveArtists() == null ? List.of()
                                : live.getLiveArtists().stream()
                                .map(link -> ArtistSummaryDto.fromEntity(link.getArtist()))
                                .toList()
                )
                .schedules(List.of(LiveScheduleDto.fromEntity(schedule)))
                .countBookmark(
                        live.getBookmarks() == null ? 0 : live.getBookmarks().size()
                )
                .isBookmarked(true)
                .build();
    }

    public List<LiveSummaryResponse> getUpcomingLivesOfFollowedArtists(Long userId) {

        final User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("유저를 찾을 수 없습니다. ID: " + userId));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<LiveSchedule> schedules =
                userArtistRepository.findFutureSchedulesOfFollowedArtists(
                        currentUser,
                        RequestStatus.APPROVED,
                        today,
                        now
                );

        Map<Long, LiveWithSchedules> grouped = new LinkedHashMap<>();

        for (LiveSchedule ls : schedules) {
            Live live = ls.getLive();
            Schedule schedule = ls.getSchedule();
            Long liveId = live.getId();

            LiveWithSchedules group = grouped.get(liveId);
            if (group == null) {
                group = new LiveWithSchedules(live, new ArrayList<>());
                grouped.put(liveId, group);
            }
            group.schedules().add(schedule);
        }

        List<LiveSummaryResponse> result = new ArrayList<>();

        for (LiveWithSchedules group : grouped.values()) {
            Live live = group.live();
            List<Schedule> futureSchedules = group.schedules();

            boolean isBookmarked = bookmarkRepository.existsByUserAndLive(currentUser, live);

            LiveSummaryResponse dto = LiveSummaryResponse.builder()
                    .id(live.getId())
                    .title(live.getTitle())
                    .posterUrl(live.getPosterUrl())
                    .ticketUrl(live.getTicketUrl())
                    .ticketDateTime(live.getTicketDateTime())
                    .artists(
                            live.getLiveArtists() == null ? List.of()
                                    : live.getLiveArtists().stream()
                                    .map(link -> ArtistSummaryDto.fromEntity(link.getArtist()))
                                    .toList()
                    )
                    .schedules(
                            futureSchedules.stream()
                                    .map(LiveScheduleDto::fromEntity)
                                    .toList()
                    )
                    .countBookmark(
                            live.getBookmarks() == null ? 0 : live.getBookmarks().size()
                    )
                    .isBookmarked(isBookmarked)
                    .build();

            result.add(dto);
        }

        return result;
    }
    private record LiveWithSchedules(
            Live live,
            List<Schedule> schedules
    ) {
    }

    public List<BookmarkReviewResponse> getBookmarkedLiveReviews(Long userId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("유저를 찾을 수 없습니다. ID: " + userId));

        List<Post> posts = postRepository.findReviewPostsForBookmarkedLives(
                currentUser,
                RequestStatus.APPROVED,
                CommunityCategoryType.REVIEW
        );

        return posts.stream()
                .map(BookmarkReviewResponse::fromEntity)
                .toList();
    }
}

