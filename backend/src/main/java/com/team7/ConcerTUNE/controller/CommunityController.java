package com.team7.ConcerTUNE.controller;

import com.team7.ConcerTUNE.dto.*;
import com.team7.ConcerTUNE.entity.CommunityCategoryType;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.service.AuthService;
import com.team7.ConcerTUNE.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommunityController {

    private final CommunityService communityService;
    private final AuthService authService;
    private CommunityCategoryType parseCategory(String categoryPath) {
        try {
            return CommunityCategoryType.valueOf(categoryPath.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid community category: " + categoryPath);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/posts/{category}")
    public ResponseEntity<PostDetailResponse> createPost(
            @PathVariable String category,
            @Valid @RequestBody PostCreateRequest request,
            Authentication authentication
    ) {
        User user = authService.getUserFromAuth(authentication);
        CommunityCategoryType categoryEnum = parseCategory(category);
        PostDetailResponse response = communityService.createPost(categoryEnum, request, user.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/posts/category/{category}")
    public ResponseEntity<Page<PostListResponse>> getPostsByCategory(
            @PathVariable String category,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        CommunityCategoryType categoryEnum = parseCategory(category);
        Page<PostListResponse> posts = communityService.getPostsByCategory(categoryEnum, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/live/{liveId}/category/{category}")
    public ResponseEntity<Page<PostListResponse>> getPostsByLiveAndCategory(
            @PathVariable Long liveId,
            @PathVariable String category,
            @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable
    ) {
        System.out.println("ㅎㅇ");
        CommunityCategoryType categoryEnum = parseCategory(category);
        Page<PostListResponse> posts = communityService.getPostsByLiveAndCategory(liveId, categoryEnum, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(
            @PathVariable Long postId
    ) {
        PostDetailResponse response = communityService.getPost(postId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostDetailResponse> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request,
            Authentication authentication
    ) {
        User user = authService.getUserFromAuth(authentication);
        PostDetailResponse response = communityService.updatePost(postId, request, user.getId());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        User user = authService.getUserFromAuth(authentication);
        communityService.deletePost(postId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<PostListResponse>> searchPosts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.ok(Page.empty(pageable));
        }
        Page<PostListResponse> posts = communityService.searchPosts(keyword, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/bookmarked")
    public ResponseEntity<List<BookmarkedPostDto>> getBookmarkedConcertPosts(Authentication authentication) {
        List<BookmarkedPostDto> posts = communityService.getBookmarkedConcertPosts(authentication);

        return ResponseEntity.ok(posts);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Boolean> togglePostLike(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        User user = authService.getUserFromAuth(authentication);
        boolean isLiked = communityService.togglePostLike(postId, user.getId());
        return ResponseEntity.ok(isLiked);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/posts/{postId}/like/status")
    public ResponseEntity<Boolean> isPostLiked(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        User user = authService.getUserFromAuth(authentication);
        boolean isLiked = communityService.isPostLiked(postId, user.getId());
        return ResponseEntity.ok(isLiked);
    }


    @GetMapping("/posts/top-weekly")
    public ResponseEntity<List<PostSummaryDto>> getTop3WeeklyPosts() {
        List<PostSummaryDto> topPosts = communityService.getTop3WeeklyPosts();
        return ResponseEntity.ok(topPosts);
    }


    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(
            @PathVariable Long postId
    ) {
        List<CommentResponse> comments = communityService.getCommentsByPost(postId);
        return ResponseEntity.ok(comments);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        User user = authService.getUserFromAuth(authentication);

        CommentResponse response = communityService.createComment(postId, request, user.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        User user = authService.getUserFromAuth(authentication);

        CommentResponse response = communityService.updateComment(commentId, request, user.getId());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        User user = authService.getUserFromAuth(authentication);

        communityService.deleteComment(commentId, user.getId());
        return ResponseEntity.noContent().build();
    }
}