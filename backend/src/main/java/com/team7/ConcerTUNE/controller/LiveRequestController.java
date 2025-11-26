package com.team7.ConcerTUNE.temp.controller;

import com.team7.ConcerTUNE.service.UserService;
import com.team7.ConcerTUNE.temp.dto.LiveRequestCreateDto;
import com.team7.ConcerTUNE.temp.dto.LiveRequestResponse;
import com.team7.ConcerTUNE.temp.dto.LiveRequestUpdateStatusDto;
import com.team7.ConcerTUNE.temp.service.LiveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/liveRequest")
public class LiveRequestController {

    private final LiveRequestService liveRequestService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LiveRequestResponse> createLiveRequest(
            @RequestBody LiveRequestCreateDto dto,
            Authentication authentication
    ) {
        LiveRequestResponse response = liveRequestService.createLiveRequest(dto, authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my-list")
    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 본인 목록을 볼 수 있도록 제한
    public ResponseEntity<Page<LiveRequestResponse>> getMyLiveRequestList(
            Pageable pageable,
            Authentication authentication
    ) {
        // Service 메서드는 Authentication 객체에서 현재 사용자의 ID를 추출하여 목록을 필터링해야 합니다.
        return ResponseEntity.ok(liveRequestService.findMyLiveRequests(pageable, authentication));
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<LiveRequestResponse>> getLiveRequestList(Pageable pageable) {
        return ResponseEntity.ok(liveRequestService.findAllLiveRequests(pageable));
    }

    @PatchMapping("/{id}/response")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LiveRequestResponse> respondToLiveRequest(
            @PathVariable Long id,
            @RequestBody LiveRequestUpdateStatusDto dto
    ) {
        return ResponseEntity.ok(liveRequestService.respondToLiveRequest(id, dto));
    }
}