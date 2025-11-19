package com.team7.ConcerTUNE.controller;

import com.team7.ConcerTUNE.dto.LiveRequestRequest;
import com.team7.ConcerTUNE.dto.LiveRequestResponse;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.security.SimpleUserDetails;
import com.team7.ConcerTUNE.service.LiveRequestService;
import com.team7.ConcerTUNE.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/liveRequest")
@RequiredArgsConstructor
public class LiveRequestController {
    private final UserService userservice;
    private final LiveRequestService liveRequestService;


    @PostMapping
    public ResponseEntity<LiveRequestResponse> createRequest(@Valid @RequestBody LiveRequestRequest request, @AuthenticationPrincipal SimpleUserDetails principal) {
        User user = userservice.findEntityById(principal.getUserId());
        LiveRequestResponse response = liveRequestService.createRequest(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LiveRequestResponse>> getRequests(Authentication authentication) {
        List<LiveRequestResponse> liveRequests = liveRequestService.getLiveRequests(authentication);
        return ResponseEntity.ok(liveRequests);
    }

    @GetMapping("/{liveRequestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LiveRequestResponse> getRequest(@PathVariable Long liveRequestId, Authentication authentication) {
        LiveRequestResponse liveRequest = liveRequestService.getLiveRequest(liveRequestId, authentication);
        return ResponseEntity.ok(liveRequest);
    }

    @PatchMapping("/{liveRequestId}/approve")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> approveRequest(@PathVariable Long liveRequestId, Authentication authentication) {
        liveRequestService.approveRequest(liveRequestId, authentication);
        return ResponseEntity.ok().build();
    }
}
