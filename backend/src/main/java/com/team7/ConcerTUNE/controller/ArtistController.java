package com.team7.ConcerTUNE.controller;

import com.team7.ConcerTUNE.dto.ArtistDetailDto;
import com.team7.ConcerTUNE.dto.ArtistSummaryDto;
import com.team7.ConcerTUNE.entity.Artist;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.entity.UserArtist;
import com.team7.ConcerTUNE.service.ArtistService;
import com.team7.ConcerTUNE.service.AuthService;
import com.team7.ConcerTUNE.temp.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;

    @GetMapping
    public ResponseEntity<Page<ArtistSummaryDto>> getArtistList(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<ArtistSummaryDto> artistPage = artistService.getArtistList(name, pageable);
        return ResponseEntity.ok(artistPage);
    }

    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistDetailDto> getArtistDetails(@PathVariable Long artistId) {
        ArtistDetailDto artistDto = artistService.getArtistById(artistId);
        return ResponseEntity.ok(artistDto);
    }

    @GetMapping("/{artistId}/follow/status")
    public ResponseEntity<FollowStatusResponse> getFollowStatus(@PathVariable Long artistId, Authentication authentication) {
        FollowStatusResponse response = artistService.getFollowStatus(artistId, authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{artistId}/follow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> toggleFollow(
            @PathVariable Long artistId,
            Authentication authentication
    ) {
        boolean isFollowed = artistService.toggleFollow(artistId, authentication);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{artistId}/image")
    public ResponseEntity<Void> updateArtistImage(
            @PathVariable Long artistId,
            @RequestBody UpdateArtistImageRequest request,
            Authentication authentication
    ) {
        artistService.updateArtistImage(artistId, request.getImageUrl(), authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{artistId}")
    public ResponseEntity<Void> updateArtist(
            @PathVariable Long artistId,
            @RequestBody UpdateArtistRequest request,
            Authentication authentication
    ) {
        artistService.updateArtist(
                artistId,
                request.getArtistName(),
                request.getSnsUrl(),
                request.getGenres(),
                authentication
        );
        return ResponseEntity.ok().build();
    }
}

