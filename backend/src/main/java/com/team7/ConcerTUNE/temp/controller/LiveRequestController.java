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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/liveRequest")
public class LiveRequestController {

    private final LiveRequestService liveRequestService;

    @PostMapping
    public ResponseEntity<LiveRequestResponse> createLiveRequest(
            @RequestBody LiveRequestCreateDto dto,
            Authentication authentication
    ) {
        LiveRequestResponse response = liveRequestService.createLiveRequest(dto, authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<LiveRequestResponse>> getLiveRequestList(Pageable pageable) {
        // TODO: 인증된 사용자(관리자) 권한 검증 로직 추가
        return ResponseEntity.ok(liveRequestService.findAllLiveRequests(pageable));
    }

    @PatchMapping("/{id}/response")
    public ResponseEntity<LiveRequestResponse> respondToLiveRequest(@PathVariable Long id, @RequestBody LiveRequestUpdateStatusDto dto) {
        return ResponseEntity.ok(liveRequestService.respondToLiveRequest(id, dto));
    }
}