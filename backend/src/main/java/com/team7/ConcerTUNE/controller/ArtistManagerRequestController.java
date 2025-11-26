package com.team7.ConcerTUNE.controller;

import com.team7.ConcerTUNE.dto.ArtistManagerRequestCreateDto;
import com.team7.ConcerTUNE.dto.ArtistManagerRequestResponse;
import com.team7.ConcerTUNE.dto.ArtistManagerRequestStatusUpdateDto;
import com.team7.ConcerTUNE.entity.ArtistManagerRequest;
import com.team7.ConcerTUNE.service.ArtistManagerRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @PostMapping
    public ResponseEntity<Void> createRequest(
            @RequestBody ArtistManagerRequestCreateDto createDto,
            Authentication authentication) {

        requestService.submitRequest(createDto, authentication);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ArtistManagerRequestResponse>> getMyRequests(Authentication authentication, Pageable pageable) {
        Page<ArtistManagerRequest> requestsPage = requestService.getMyRequests(authentication, pageable);

        Page<ArtistManagerRequestResponse> responsePage = requestsPage
                .map(ArtistManagerRequestResponse::from);

        return ResponseEntity.ok(responsePage);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ArtistManagerRequestResponse>> getAllRequests(Pageable pageable) {
        Page<ArtistManagerRequest> requestsPage = requestService.getAllRequests(pageable);

        Page<ArtistManagerRequestResponse> responsePage = requestsPage
                .map(ArtistManagerRequestResponse::from);

        return ResponseEntity.ok(responsePage);
    }

    @PatchMapping("/{id}/response")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArtistManagerRequestResponse> respondToManagerRequest(
            @PathVariable("id") Long id,
            @RequestBody @Valid ArtistManagerRequestStatusUpdateDto dto)
    {

        ArtistManagerRequest processedRequest = requestService.respondToManagerRequest(id, dto);
        return ResponseEntity.ok(ArtistManagerRequestResponse.from(processedRequest));
    }
}