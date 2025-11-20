package com.team7.ConcerTUNE.temp.service;

import com.team7.ConcerTUNE.entity.LiveRequest;
import com.team7.ConcerTUNE.entity.RequestStatus;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.service.ArtistService;
import com.team7.ConcerTUNE.service.AuthService;
import com.team7.ConcerTUNE.temp.dto.LiveRequestCreateDto;
import com.team7.ConcerTUNE.temp.dto.LiveRequestResponse;
import com.team7.ConcerTUNE.temp.dto.LiveRequestUpdateStatusDto;
import com.team7.ConcerTUNE.temp.repository.LiveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LiveRequestService {

    private final LiveRequestRepository liveRequestRepository;
    private final ArtistService artistService;
    private final AuthService authService;
    private final LiveService liveService;

    @Transactional
    public LiveRequestResponse createLiveRequest(LiveRequestCreateDto dto, Authentication authentication) {

        User user = authService.getUserFromAuth(authentication);

        List<Long> finalArtistIds = new ArrayList<>();
        if (dto.getExistingArtistIds() != null) {
            finalArtistIds.addAll(dto.getExistingArtistIds());
        }

        LiveRequest liveRequest = LiveRequest.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .posterUrl(dto.getPosterUrl())
                .ticketUrl(dto.getTicketUrl())
                .venue(dto.getVenue())
                .seatPrices(dto.getSeatPrices())
                .artistIds(finalArtistIds)
                .newArtistRequestData(dto.getNewArtistRequests())
                .requester(user)
                .requestedSchedules(dto.getSchedules())
                .requestStatus(RequestStatus.PENDING)
                .requestCreatedAt(LocalDateTime.now())
                .build();

        LiveRequest savedRequest = liveRequestRepository.save(liveRequest);
        List<String> artistNames = artistService.getArtistNamesByIds(savedRequest.getArtistIds());

        return LiveRequestResponse.fromEntity(savedRequest, artistNames);
    }


    @Transactional(readOnly = true)
    public Page<LiveRequestResponse> findAllLiveRequests(Pageable pageable) {

        Page<LiveRequest> liveRequestPage = liveRequestRepository.findAll(pageable);

        return liveRequestPage.map(liveRequest -> {
            List<String> artistNames = artistService.getArtistNamesByIds(liveRequest.getArtistIds());

            return LiveRequestResponse.fromEntity(liveRequest, artistNames);
        });
    }

    @Transactional(readOnly = true)
    public Page<LiveRequestResponse> findMyLiveRequests(Pageable pageable, Authentication authentication) {
        User user = authService.getUserFromAuth(authentication);


        Page<LiveRequest> myRequests = liveRequestRepository.findByRequesterId(user.getId(), pageable);

        return myRequests.map(liveRequest -> {
            List<Long> artistIds = liveRequest.getArtistIds();

            List<String> artistNames = artistService.getArtistNamesByIds(artistIds);

            return LiveRequestResponse.fromEntity(liveRequest, artistNames);
        });
    }

    @Transactional
    public LiveRequestResponse respondToLiveRequest(Long requestId, LiveRequestUpdateStatusDto dto) {

        LiveRequest liveRequest = liveRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("LiveRequest not found with id: " + requestId));

        RequestStatus newStatus = dto.getStatus();

        liveRequest.changeStatus(newStatus);

        if (newStatus == RequestStatus.REJECTED) {
            liveRequest.setRejectionReason(dto.getRejectionReason());
        } else {
            liveRequest.setRejectionReason(null);
        }

        if (newStatus == RequestStatus.APPROVED) {
            try {
                // LiveService를 통해 Live 엔티티 및 관련 일정/아티스트 연결 생성
                liveService.createLiveFromRequest(liveRequest);
            } catch (Exception e) {
                // Live 생성 실패 시, 트랜잭션을 롤백하거나 상태를 PENDING 등으로 되돌리는 처리 필요
                // 여기서는 일단 예외를 던져 트랜잭션 롤백을 유도
                throw new RuntimeException("Live 생성 중 오류가 발생했습니다.", e);
            }
        }

        // 4. 응답 DTO 반환
        List<String> artistNames = artistService.getArtistNamesByIds(liveRequest.getArtistIds());
        return LiveRequestResponse.fromEntity(liveRequest, artistNames);
    }
}