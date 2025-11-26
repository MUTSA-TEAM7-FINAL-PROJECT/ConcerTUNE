package com.team7.ConcerTUNE.service;

import com.team7.ConcerTUNE.dto.ArtistManagerRequestDto;
import com.team7.ConcerTUNE.entity.*;
import com.team7.ConcerTUNE.exception.ResourceNotFoundException;
import com.team7.ConcerTUNE.repository.ArtistManagerRepository;
import com.team7.ConcerTUNE.repository.ArtistManagerRequestRepository;
import com.team7.ConcerTUNE.repository.ArtistRepository;
import com.team7.ConcerTUNE.repository.UserRepository;
import com.team7.ConcerTUNE.security.SimpleUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtistManagerRequestService {

    private final ArtistManagerRequestRepository requestRepository;
    private final ArtistRepository artistRepository;
    private final ArtistManagerRepository artistManagerRepository;
    private final UserRepository userRepository; // 유저 조회를 위해 추가
    private final NotificationService notificationService;

    // [추가] 1. 사용자 요청 등록 메서드 (Controller에서 호출)
    public void submitManagerRequest(ArtistManagerRequestDto requestDto, Authentication authentication) {
        // 로그인 유저 정보 가져오기
        User user = getUserFromAuth(authentication);

        // 요청 엔티티 생성 및 저장
        ArtistManagerRequest request = ArtistManagerRequest.builder()
                .user(user)
                .artistId(requestDto.getArtistId()) // DTO에서 받은 ID 저장 (엔티티에 필드 추가 필요)
                .artistName(requestDto.getArtistName()) // 이름도 저장 (검색 편의성)
                .description(requestDto.getDescription()) // DTO의 reason -> Entity의 description
                .proofDocumentUrl(requestDto.getProofDocumentUrl())
                .status(RequestStatus.PENDING)
                .build();

        requestRepository.save(request);
    }

    // [수정] 2. 관리자 승인 메서드
    public void approveRequest(Long requestId, String adminNote) {
        // 1. 요청 조회
        ArtistManagerRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("요청을 찾을 수 없습니다."));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        // 2. 요청 상태 변경 (APPROVED)
        request.setStatus(RequestStatus.APPROVED);
        request.setAdminNote(adminNote);

        // 3. [수정] 기존 아티스트 조회 (새로 생성 X)
        // 만약 artistId가 null이라면(혹시 모를 예외) artistName으로 찾거나 예외 처리
        Artist artist = null;
        if (request.getArtistId() != null) {
            artist = artistRepository.findById(request.getArtistId())
                    .orElseThrow(() -> new ResourceNotFoundException("해당 아티스트를 찾을 수 없습니다."));
        } else {
            // ID가 없는 레거시 데이터가 있다면 이름으로 검색 (선택 사항)
            artist = artistRepository.findByArtistName(request.getArtistName())
                    .orElseThrow(() -> new ResourceNotFoundException("해당 이름의 아티스트를 찾을 수 없습니다."));
        }

        // (옵션) 이미 매니저가 있는지 확인
        if (artistManagerRepository.existsByIdArtistIdAndIdUserId(artist.getArtistId(), request.getUser().getId())) {
            throw new IllegalStateException("이미 해당 아티스트의 매니저입니다.");
        }

        // 4. 매니저 권한 등록
        ArtistManager manager = ArtistManager.builder()
                .user(request.getUser())
                .artist(artist) // 찾아온 기존 아티스트 연결
                .assignedAt(LocalDateTime.now())
                .isOfficial(true)
                .build();
        artistManagerRepository.save(manager);

        // 5. 알림 발송
        String content = "축하합니다! '" + artist.getArtistName() + "' 아티스트 매니저 권한 요청이 승인되었습니다.";
        String link = "/artists/" + artist.getArtistId();
        notificationService.createNotification(request.getUser(), content, link);
    }

    // 유틸리티: 인증 정보에서 유저 추출
    private User getUserFromAuth(Authentication authentication) {
        SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
        return userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("유저를 찾을 수 없습니다."));
    }
}