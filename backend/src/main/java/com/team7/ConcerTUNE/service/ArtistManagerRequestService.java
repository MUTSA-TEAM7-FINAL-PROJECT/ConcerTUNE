package com.team7.ConcerTUNE.temp.service;

import com.team7.ConcerTUNE.entity.*;
import com.team7.ConcerTUNE.exception.ResourceNotFoundException;
import com.team7.ConcerTUNE.repository.ArtistManagerRepository;
import com.team7.ConcerTUNE.repository.ArtistRepository;
import com.team7.ConcerTUNE.service.AuthService;
import com.team7.ConcerTUNE.dto.ArtistManagerRequestCreateDto;
import com.team7.ConcerTUNE.dto.ArtistManagerRequestStatusUpdateDto;
import com.team7.ConcerTUNE.entity.ArtistManagerRequest;
import com.team7.ConcerTUNE.event.ArtistManagerRequestEvent;
import com.team7.ConcerTUNE.repository.ArtistManagerRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArtistManagerRequestService {

    private final ApplicationEventPublisher eventPublisher;
    private final ArtistManagerRequestRepository requestRepository;
    private final ArtistManagerRepository artistManagerRepository;
    private final ArtistRepository artistRepository;
    private final AuthService authService;

    public void submitRequest(ArtistManagerRequestCreateDto dto, Authentication authentication) {
        User user = authService.getUserFromAuth(authentication);
        Artist artist = artistRepository.findById(dto.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist를 찾을 수 없습니다."));


        ArtistManagerRequest newRequest = ArtistManagerRequest.builder()
                .artist(artist)
                .user(user)
                .reason(dto.getReason())
                .isOfficial(dto.getIsOfficial())
                .status(RequestStatus.PENDING)
                .build();

        requestRepository.save(newRequest);
    }

    public Page<ArtistManagerRequest> getMyRequests(Authentication authentication, Pageable pageable) {
        User user = authService.getUserFromAuth(authentication);
        return requestRepository.findByUserId(user.getId(), pageable);
    }

    // 관리자 전체 요청 조회 (페이지네이션 적용)
    public Page<ArtistManagerRequest> getAllRequests(Pageable pageable) {
        return requestRepository.findAll(pageable);
    }


    @Transactional
    public ArtistManagerRequest respondToManagerRequest(Long requestId, ArtistManagerRequestStatusUpdateDto dto) { // adminId 매개변수 제거

        if (dto.getStatus() == RequestStatus.APPROVED) {
            return this.performApproval(requestId, dto.getAdminNote());
        } else if (dto.getStatus() == RequestStatus.REJECTED) {
            return this.performRejection(requestId, dto.getAdminNote());
        } else {
            throw new IllegalArgumentException("요청 상태는 APPROVED 또는 REJECTED만 가능합니다.");
        }
    }


    private ArtistManagerRequest performApproval(Long requestId, String adminNote) { // adminId 매개변수 제거
        ArtistManagerRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("요청 ID를 찾을 수 없습니다: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다. 현재 상태: " + request.getStatus());
        }
        request.approve(adminNote);
        requestRepository.save(request);

        User requestedUser = request.getUser();
        Artist requestedArtist = request.getArtist();

        Optional<ArtistManager> existingManagerOpt =
                artistManagerRepository.findByUserAndArtist(requestedUser, requestedArtist);

        if (existingManagerOpt.isPresent()) {
            ArtistManager existingManager = existingManagerOpt.get();
            if (request.isOfficial()) {
                existingManager.setOfficial(true);
                artistManagerRepository.save(existingManager);
            }
        } else {
            ArtistManagerId managerId = new ArtistManagerId(requestedUser.getId(), requestedArtist.getArtistId());

            ArtistManager newManager = ArtistManager.builder()
                    .id(managerId)
                    .user(requestedUser)
                    .artist(requestedArtist)
                    .assignedAt(LocalDateTime.now())
                    .isOfficial(request.isOfficial())
                    .build();

            artistManagerRepository.save(newManager);
        }
        eventPublisher.publishEvent(new ArtistManagerRequestEvent(this, request.getUser(), true));

        return request;
    }

    private ArtistManagerRequest performRejection(Long requestId, String adminNote) { // adminId 매개변수 제거
        ArtistManagerRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("요청 ID를 찾을 수 없습니다: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다. 현재 상태: " + request.getStatus());
        }
        request.reject( adminNote);
        eventPublisher.publishEvent(new ArtistManagerRequestEvent(this, request.getUser(), false));

        return requestRepository.save(request);
    }
}