package com.team7.ConcerTUNE.temp.service;

import com.team7.ConcerTUNE.dto.*;
import com.team7.ConcerTUNE.entity.*;
import com.team7.ConcerTUNE.repository.*;
import com.team7.ConcerTUNE.service.AuthService;
import com.team7.ConcerTUNE.event.LiveCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LiveService {

    private final ApplicationEventPublisher eventPublisher;
    private final LiveRepository liveRepository;
    private final UserGenrePreferenceRepository userGenrePreferenceRepository;
    private final AuthService authService;
    private final ArtistRepository artistRepository;
    private final SchedulesRepository schedulesRepository;
    private final LiveArtistRepository liveArtistRepository;
    private final LiveSchedulesRepository liveSchedulesRepository;

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

    @Transactional
    public Lives createLiveFromRequest(LiveRequest liveRequest) {

        // 1. Lives 엔티티 생성 및 저장
        Lives live = Lives.builder()
                .title(liveRequest.getTitle())
                .description(liveRequest.getDescription())
                .posterUrl(liveRequest.getPosterUrl())
                .ticketUrl(liveRequest.getTicketUrl())
                .venue(liveRequest.getVenue())
                .seatPrices(liveRequest.getSeatPrices())
                .build();

        Lives savedLive = liveRepository.save(live);

        // 2. 아티스트 처리 (기존 아티스트 + 신규 아티스트 생성)
        List<Artist> artistsToLink = new ArrayList<>();

        // 2-1. 기존 아티스트 (ID 목록) 조회
        if (liveRequest.getArtistIds() != null && !liveRequest.getArtistIds().isEmpty()) {
            List<Artist> existingArtists = artistRepository.findAllById(liveRequest.getArtistIds());
            artistsToLink.addAll(existingArtists);
        }

        // 2-2. 신규 아티스트 요청 처리 및 엔티티 생성
        if (liveRequest.getNewArtistRequestData() != null && !liveRequest.getNewArtistRequestData().isEmpty()) {
            for (NewArtistRequestDto dto : liveRequest.getNewArtistRequestData()) {

                Artist newArtist = Artist.builder()
                        .artistName(dto.getName())
                        .isDomestic(dto.getIsDomestic())
                        .build();

                Artist savedArtist = artistRepository.save(newArtist);
                artistsToLink.add(savedArtist);
            }
        }

        // 2-3. LiveArtist 연결 생성 및 저장
        if (!artistsToLink.isEmpty()) {
            List<LiveArtist> liveArtists = artistsToLink.stream()
                    .map(artist -> LiveArtist.builder()
                            .live(savedLive)
                            .artist(artist) // 💡 Artist 엔티티 직접 연결
                            .build())
                    .toList();

            liveArtistRepository.saveAll(liveArtists);
            savedLive.setLiveArtists(liveArtists); // Lives 엔티티의 컬렉션에도 반영 (Optional)
        }

        // 3. LiveSchedules 및 Schedules 생성 (일정 처리)
        List<LiveSchedules> newLiveSchedules = new ArrayList<>();

        if (liveRequest.getRequestedSchedules() != null && !liveRequest.getRequestedSchedules().isEmpty()) {

            for (ScheduleDto scheduleDto : liveRequest.getRequestedSchedules()) {

                LocalDate date = scheduleDto.getLiveDate();
                LocalTime time = scheduleDto.getLiveTime();

                // 3-1. 기존 Schedules 엔티티가 있는지 확인
                Optional<Schedules> existingSchedule = schedulesRepository.findByLiveDateAndLiveTime(date, time);

                Schedules schedule;

                if (existingSchedule.isPresent()) {
                    schedule = existingSchedule.get();
                } else {
                    // 3-2. 새로운 Schedules 엔티티 생성 및 저장
                    schedule = Schedules.builder()
                            .liveDate(date)
                            .liveTime(time)
                            .build();
                    schedule = schedulesRepository.save(schedule);
                }

                // 3-3. LiveSchedules (중간 테이블) 엔티티 생성
                LiveSchedules liveSchedule = LiveSchedules.builder()
                        .live(savedLive)
                        .schedule(schedule)
                        .build();

                newLiveSchedules.add(liveSchedule);
            }

            liveSchedulesRepository.saveAll(newLiveSchedules);
            savedLive.setLiveSchedules(newLiveSchedules);
        }
        for (Artist artist : savedLive.getLiveArtists().stream().map(LiveArtist::getArtist).toList()) {
            eventPublisher.publishEvent(new LiveCreatedEvent(this, artist, savedLive.getId()));
        }
        return savedLive;
    }

}