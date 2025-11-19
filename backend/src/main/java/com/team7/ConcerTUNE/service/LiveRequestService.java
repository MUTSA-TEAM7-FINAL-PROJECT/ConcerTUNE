package com.team7.ConcerTUNE.service;

import com.team7.ConcerTUNE.dto.LiveRequestRequest;
import com.team7.ConcerTUNE.dto.LiveRequestResponse;
import com.team7.ConcerTUNE.dto.ScheduleCreateRequest;
import com.team7.ConcerTUNE.dto.UserResponse;
import com.team7.ConcerTUNE.entity.*;
import com.team7.ConcerTUNE.exception.BadRequestException;
import com.team7.ConcerTUNE.exception.ResourceNotFoundException;
import com.team7.ConcerTUNE.repository.*;
import com.team7.ConcerTUNE.security.SimpleUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LiveRequestService {
    private final LiveRequestRepository liveRequestRepository;
    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;
    private final LiveRepository liveRepository;
    private final ScheduleRepository scheduleRepository;

    // 요청 등록
    public LiveRequestResponse createRequest(LiveRequestRequest request, User user) {
        LiveRequest liveRequest = LiveRequest.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .posterUrl(request.getPosterUrl())
                .ticketUrl(request.getTicketUrl())
                .venue(request.getVenue())
                .price(request.getPrice())
                .user(user)
                .build();

        for (Long artistId : request.getArtistIds()) {
            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new RuntimeException("아티스트를 찾을 수 없습니다: " + artistId));

            LiveRequestArtist join = LiveRequestArtist.builder()
                    .liveRequest(liveRequest)
                    .artist(artist)
                    .build();

            liveRequest.getLiveRequestArtists().add(join);
        }

        for (ScheduleCreateRequest scheduleRequest : request.getSchedules()) {

            Schedule schedule = Schedule.builder()
                    .liveDate(scheduleRequest.getLiveDate())
                    .liveTime(scheduleRequest.getStartTime())
                    .build();

            scheduleRepository.save(schedule);

            LiveRequestSchedule join = LiveRequestSchedule.builder()
                    .liveRequest(liveRequest)
                    .schedule(schedule)
                    .build();

            liveRequest.getLiveRequestSchedules().add(join);
        }

        liveRequestRepository.save(liveRequest);

        return LiveRequestResponse.fromEntity(liveRequest);
    }

    // 요청 목록 반환
    public Page<LiveRequestResponse> getAllRequests(Pageable pageable) {
        Page<LiveRequest> liveRequests = liveRequestRepository.findAllByRequestStatus(RequestStatus.PENDING, pageable);
        return liveRequests.map(liveRequest -> {
            LiveRequestResponse response = LiveRequestResponse.fromEntity(liveRequest);
            return response;
        });
    }

    // 요청 개별 반환
    public LiveRequestResponse getLiveRequest(Long liveRequestId) {
        LiveRequest liveRequest = liveRequestRepository.findById(liveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("요청을 찾을 수 없습니다. ID: " + liveRequestId));

        return LiveRequestResponse.fromEntity(liveRequest);
    }

    // 요청 승인
    public void approveRequest(Long liveRequestId) {
        LiveRequest liveRequest = liveRequestRepository.findById(liveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("요청을 찾을 수 없습니다. ID: " + liveRequestId));

        liveRequest.changeStatus(RequestStatus.APPROVED);

        Live live = Live.builder()
                .title(liveRequest.getTitle())
                .description(liveRequest.getDescription())
                .posterUrl(liveRequest.getPosterUrl())
                .ticketUrl(liveRequest.getTicketUrl())
                .venue(liveRequest.getVenue())
                .price(liveRequest.getPrice())
                .build();

        List<LiveArtist> liveArtists = liveRequest.getLiveRequestArtists().stream()
                .map(liveRequestArtist -> LiveArtist.builder()
                        .live(live)                      // 새로 만든 Live에 연결
                        .artist(liveRequestArtist.getArtist())    // 요청에 달려 있던 Artist 재사용
                        .build())
                .toList();

        live.setLiveArtists(liveArtists);

        List<LiveSchedule> liveSchedules = liveRequest.getLiveRequestSchedules().stream()
                .map(reqSch -> LiveSchedule.builder()
                        .live(live)
                        .schedule(reqSch.getSchedule())
                        .build())
                .toList();
        live.setLiveSchedules(liveSchedules);

        liveRepository.save(live);
        liveRequestRepository.save(liveRequest);

    }

    // 편의 메서드
    private User getAdminFromAuth(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof SimpleUserDetails)) {
            throw new BadRequestException("유효한 로그인 정보가 없습니다. (Auth is null or not SimpleUserDetails)");
        }
        SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();

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
