package com.team7.ConcerTUNE.controller;

import com.team7.ConcerTUNE.dto.CommentCreateRequest;
import com.team7.ConcerTUNE.dto.CommentUpdateRequest;
import com.team7.ConcerTUNE.dto.CommentResponse;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.service.AuthService;
import com.team7.ConcerTUNE.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.team7.ConcerTUNE.security.SimpleUserDetails;

import java.util.List;

// 댓글 관련 API 컨트롤러
@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

	private final CommentService commentService;
	private final AuthService authService;

	// 게시글의 댓글 조회
	@GetMapping("/api/posts/{postId}/comments")
	public ResponseEntity<List<CommentResponse>> getComments(
			@PathVariable Long postId
	) {
		log.info("댓글 조회 요청: postId={}", postId);
		List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
		return ResponseEntity.ok(comments);
	}

	// 댓글 작성
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/api/posts/{postId}/comments")
	public ResponseEntity<CommentResponse> createComment(
			@PathVariable Long postId,
			@Valid @RequestBody CommentCreateRequest request,
			Authentication authentication
		) {
		SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
		Long userId = userDetails.getUserId();
		log.info("댓글 작성 요청: postId={}, userId={}, parentCommentId={}", postId, userId, request.getParentCommentId());
		CommentResponse comment = commentService.createComment(postId, request, userId);
		return ResponseEntity.ok(comment);
	}

	// 댓글 수정
	@PreAuthorize("isAuthenticated()")
	@PutMapping("/api/comments/{commentId}")
	public ResponseEntity<CommentResponse> updateComment(
			@PathVariable Long commentId,
			@Valid @RequestBody CommentUpdateRequest request,
			Authentication authentication
		) {
		SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
		Long userId = userDetails.getUserId();
		log.info("댓글 수정 요청: commentId={}, userId={}", commentId, userId);
		CommentResponse comment = commentService.updateComment(commentId, request, userId);
		return ResponseEntity.ok(comment);
	}

	// 댓글 삭제
	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/api/comments/{commentId}")
	public ResponseEntity<Void> deleteComment(
			@PathVariable Long commentId,
			Authentication authentication
		) {
		SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
		Long userId = userDetails.getUserId();
		log.info("댓글 삭제 요청: commentId={}, userId={}", commentId, userId);
		commentService.deleteComment(commentId, userId);
		return ResponseEntity.ok().build();
	}

	// 댓글 좋아요
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/api/comments/{commentId}/like")
	public ResponseEntity<CommentResponse> likeComment(
			@PathVariable Long commentId,
			Authentication authentication
		) {
		SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
		Long userId = userDetails.getUserId();
		log.info("댓글 좋아요 요청: commentId={}, userId={}", commentId, userId);
		CommentResponse comment = commentService.likeComment(commentId, userId);
		return ResponseEntity.ok(comment);
	}

	// 댓글 좋아요 취소
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/api/comments/{commentId}/dislike")
	public ResponseEntity<CommentResponse> dislikeComment(
			@PathVariable Long commentId,
			Authentication authentication
		) {
		SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
		Long userId = userDetails.getUserId();
		log.info("댓글 좋아요 취소 요청: commentId={}, userId={}", commentId, userId);
		CommentResponse comment = commentService.dislikeComment(commentId, userId);
		return ResponseEntity.ok(comment);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/api/comments/{commentId}/like/status")
	public ResponseEntity<Boolean> isCommentLiked(
			@PathVariable Long commentId,
			Authentication authentication
	) {
		User user = authService.getUserFromAuth(authentication);
		boolean isLiked = commentService.isCommentLiked(commentId, user.getId());
		return ResponseEntity.ok(isLiked);
	}
}
