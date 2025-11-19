package com.team7.ConcerTUNE.temp.service;

import com.team7.ConcerTUNE.entity.Artist;
import com.team7.ConcerTUNE.entity.LiveRequest;
import com.team7.ConcerTUNE.entity.RequestStatus;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.repository.ArtistRepository;
import com.team7.ConcerTUNE.repository.UserRepository;
import com.team7.ConcerTUNE.service.ArtistService;
import com.team7.ConcerTUNE.service.AuthService;
import com.team7.ConcerTUNE.service.UserService;
import com.team7.ConcerTUNE.temp.dto.LiveRequestCreateDto;
import com.team7.ConcerTUNE.temp.dto.LiveRequestResponse;
import com.team7.ConcerTUNE.temp.dto.LiveRequestUpdateStatusDto;
import com.team7.ConcerTUNE.temp.dto.NewArtistRequestDto;
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
    @Transactional
    public LiveRequestResponse respondToLiveRequest(Long requestId, LiveRequestUpdateStatusDto dto) {

        LiveRequest liveRequest = liveRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("LiveRequest not found with id: " + requestId));

        RequestStatus newStatus = dto.getStatus();
        liveRequest.changeStatus(newStatus); // 상태 변경 및 시간 업데이트

        if (newStatus == RequestStatus.APPROVED) {
            // TODO: 승인된 LiveRequest 정보를 기반으로 Lives 엔티티 생성 및 LiveSchedules, LiveArtist 연결 로직 구현
            // liveService.createLiveFromRequest(liveRequest);
        }
        List<String> artistNames = artistService.getArtistNamesByIds(liveRequest.getArtistIds());
        return LiveRequestResponse.fromEntity(liveRequest, artistNames);
    }
}