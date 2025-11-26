package com.team7.ConcerTUNE.controller;

import com.team7.ConcerTUNE.service.ArtistManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/artist-manager")
@RequiredArgsConstructor
public class ArtistManagerController {

    private final ArtistManagerService artistManagerService;

    @GetMapping("/{artistId}/is-admin")
    public ResponseEntity<Boolean> checkAdmin(
            @PathVariable Long artistId,
            Authentication authentication
    ) {
        boolean isAdmin = artistManagerService.isAdmin(artistId, authentication);
        return ResponseEntity.ok(isAdmin);
    }
}
