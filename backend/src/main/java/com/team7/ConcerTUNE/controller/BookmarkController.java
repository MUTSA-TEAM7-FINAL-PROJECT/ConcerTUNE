package com.team7.ConcerTUNE.controller;

import com.team7.ConcerTUNE.dto.BookmarkReviewResponse;
import com.team7.ConcerTUNE.dto.LiveResponse;
import com.team7.ConcerTUNE.dto.LiveSummaryResponse;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.security.SimpleUserDetails;
import com.team7.ConcerTUNE.service.BookmarkService;
import com.team7.ConcerTUNE.service.LiveService;
import com.team7.ConcerTUNE.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;
    private final UserService userService;
    private final LiveService liveService;

    @PostMapping("/{liveId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> toggleBookmark(@PathVariable Long liveId, @AuthenticationPrincipal SimpleUserDetails principal) {
        User user = userService.findEntityById(principal.getUserId());
        boolean bookmarked = bookmarkService.toggleBookmark(liveId, user);
        return ResponseEntity.ok(bookmarked);
    }

    @GetMapping("/{liveId}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> isBookmarked(@PathVariable Long liveId, @AuthenticationPrincipal SimpleUserDetails principal) {
        User user = userService.findEntityById(principal.getUserId());
        return ResponseEntity.ok (bookmarkService.isBookmarked(liveId, user));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<LiveResponse>> getBookmarkedLives(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal SimpleUserDetails principal
    ) {
        User user = userService.findEntityById(principal.getUserId());
        return ResponseEntity.ok(bookmarkService.getBookmarkedLives(pageable, user));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/nearest")
    public ResponseEntity<LiveSummaryResponse> getNearestBookmarkedLive(
            @AuthenticationPrincipal SimpleUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();

        LiveSummaryResponse response =
                liveService.getNearestBookmarkedLive(userId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/reviews")
    public ResponseEntity<List<BookmarkReviewResponse>> getBookmarkedLiveReviews(
            @AuthenticationPrincipal SimpleUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();

        List<BookmarkReviewResponse> responses =
                liveService.getBookmarkedLiveReviews(userId);

        return ResponseEntity.ok(responses);
    }
}
