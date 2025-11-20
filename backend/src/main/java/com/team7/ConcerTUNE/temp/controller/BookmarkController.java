package com.team7.ConcerTUNE.temp.controller;

import com.team7.ConcerTUNE.temp.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lives/{liveId}/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping("/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> getBookmarkStatus(@PathVariable Long liveId, Authentication authentication) {
        boolean isHearted = bookmarkService.isLiveBookmarked(liveId, authentication);
        return ResponseEntity.ok(isHearted);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> toggleBookmark(@PathVariable Long liveId, Authentication authentication) {
        boolean newStatus = bookmarkService.toggleBookmark(liveId, authentication);
        return ResponseEntity.ok(newStatus);
    }
}