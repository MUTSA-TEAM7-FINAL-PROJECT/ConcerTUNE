package com.team7.ConcerTUNE.service;

import com.team7.ConcerTUNE.dto.LiveRequest;
import com.team7.ConcerTUNE.dto.LiveResponse;
import com.team7.ConcerTUNE.entity.*;
import com.team7.ConcerTUNE.exception.BadRequestException;
import com.team7.ConcerTUNE.exception.ResourceNotFoundException;
import com.team7.ConcerTUNE.repository.*;
import com.team7.ConcerTUNE.security.SimpleUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LiveRequestService {
    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;
    private final LiveRepository liveRepository;
    private final ScheduleRepository scheduleRepository;
    private final LiveArtistRepository liveArtistRepository;
    private final LiveScheduleRepository liveScheduleRepository;

    // 요청 등록
    public LiveResponse createRequest(LiveRequest request, User user) {
        Live live = Live.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .posterUrl(request.getPosterUrl())
                .ticketUrl(request.getTicketUrl())
                .venue(request.getVenue())
                .price(request.getPrice())
                .ticketDateTime(request.getTicketDateTime())
                .writer(user)
                .build();

        // 스케줄 생성
        List<LiveSchedule> liveSchedules = Optional.ofNullable(request.getSchedules())
                .orElseGet(List::of)
                .stream()
                .map(dto -> {
                    Schedule schedule = dto.toNewScheduleEntity();
                    schedule = scheduleRepository.save(schedule);

                    return LiveSchedule.builder()
                            .live(live)
                            .schedule(schedule)
                            .build();
                })
                .toList();

        // 아티스트 링크
        List<LiveArtist> liveArtists = new ArrayList<>();
        if (request.getArtistIds() != null) {
            for (Long artistId : request.getArtistIds()) {
                Artist artist = artistRepository.findById(artistId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("존재하지 않는 아티스트 ID: " + artistId));

                liveArtists.add(
                        LiveArtist.builder()
                                .live(live)
                                .artist(artist)
                                .build()
                );
            }
        }

        live.setLiveSchedules(liveSchedules);
        live.setLiveArtists(liveArtists);

        Live saved = liveRepository.save(live);
        return LiveResponse.fromEntity(saved);
    }

    // 요청 목록 반환
    public Page<LiveResponse> getAllRequests(Pageable pageable) {
        Page<Live> liveRequests = liveRepository.findAllByRequestStatus(RequestStatus.PENDING, pageable);
        return liveRequests.map(LiveResponse::fromEntity);
    }

    // 요청 개별 반환
    public LiveResponse getLiveRequest(Long liveRequestId) {
        Live live = liveRepository.findByIdAndRequestStatus(liveRequestId, RequestStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException("요청을 찾을 수 없습니다. ID: " + liveRequestId));

        return LiveResponse.fromEntity(live);
    }

    // 요청 승인 및 공연 등록
    public void approveRequest(Long liveRequestId) {
        Live live = liveRepository.findById(liveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("요청을 찾을 수 없습니다. ID: " + liveRequestId));

        if (live.getRequestStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 공연 요청입니다. status=" + live.getRequestStatus());
        }

        live.changeStatus(RequestStatus.APPROVED);
    }

    public LiveResponse modifyLive(Long targetLiveId, LiveRequest liveRequest) {
        Live targetLive = liveRepository.findById(targetLiveId)
                .orElseThrow(() -> new ResourceNotFoundException("공연을 찾을 수 없습니다. ID: " + targetLiveId));

        targetLive.setTitle(liveRequest.getTitle());
        targetLive.setDescription(liveRequest.getDescription());
        targetLive.setPosterUrl(liveRequest.getPosterUrl());
        targetLive.setTicketUrl(liveRequest.getTicketUrl());
        targetLive.setVenue(liveRequest.getVenue());
        targetLive.setPrice(liveRequest.getPrice());
        targetLive.setTicketDateTime(liveRequest.getTicketDateTime());

        if (liveRequest.getArtistIds() != null) {
            liveArtistRepository.deleteByLive(targetLive);

            List<Artist> artists = artistRepository.findAllById(liveRequest.getArtistIds());

            List<LiveArtist> links = artists.stream()
                    .map(artist -> LiveArtist.builder()
                            .live(targetLive)
                            .artist(artist)
                            .build())
                    .toList();

            liveArtistRepository.saveAll(links);
            targetLive.getLiveArtists().clear();
            targetLive.getLiveArtists().addAll(links);
        }

        if (liveRequest.getSchedules() != null) {

            liveScheduleRepository.deleteByLive(targetLive);

            List<LiveSchedule> liveSchedules = liveRequest.getSchedules().stream()
                    .map(dto -> {
                        Schedule schedule = dto.toNewScheduleEntity();
                        schedule = scheduleRepository.save(schedule);

                        return LiveSchedule.builder()
                                .live(targetLive)
                                .schedule(schedule)
                                .build();
                    })
                    .toList();

            liveScheduleRepository.saveAll(liveSchedules);
            targetLive.getLiveSchedules().clear();
            targetLive.getLiveSchedules().addAll(liveSchedules);
        }

        return LiveResponse.fromEntity(targetLive);
    }

    // 편의 메서드
    private User getAdminFromAuth(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof SimpleUserDetails userDetails)) {
            throw new BadRequestException("유효한 로그인 정보가 없습니다. (Auth is null or not SimpleUserDetails)");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new BadRequestException("관리자만 접근할 수 있는 기능입니다.");
        }

        return userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "인증 정보에 해당하는 유저를 찾을 수 없습니다. ID: " + userDetails.getUserId()
                ));
    }
}
