package com.team7.ConcerTUNE.controller;

import com.team7.ConcerTUNE.dto.ArtistManagerRequestDto;
import com.team7.ConcerTUNE.service.ArtistManagerRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artist-manager-requests")
@RequiredArgsConstructor
public class ArtistManagerRequestController {

    private final ArtistManagerRequestService requestService;

    // 아티스트 관리자 권한 요청 등록
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> submitRequest(
            @RequestBody ArtistManagerRequestDto requestDto,
            Authentication authentication
    ) {
        requestService.submitManagerRequest(requestDto, authentication); // Service에 해당 메서드 필요 (이전 답변 참조)
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}