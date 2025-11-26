package com.team7.ConcerTUNE.controller;

import com.team7.ConcerTUNE.dto.PostCreateRequest;
import com.team7.ConcerTUNE.dto.PostUpdateRequest;
import com.team7.ConcerTUNE.dto.PostResponse;
import com.team7.ConcerTUNE.entity.CommunityCategoryType;
import com.team7.ConcerTUNE.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.team7.ConcerTUNE.security.SimpleUserDetails;

import java.util.List;

// 게시글 관련 API 컨트롤러
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

	private final PostService postService;

	// 카테고리별 게시글 조회
	@GetMapping("/{category}")
	public ResponseEntity<Page<PostResponse>> getPostsByCategory(
			@PathVariable CommunityCategoryType category,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) {
		log.info("카테고리별 게시글 조회 요청: category={}, page={}, size={}", category, page, size);
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<PostResponse> posts = postService.getPostsByCategory(category, pageable);
		return ResponseEntity.ok(posts);
	}

	// 특정 게시글 조회
	@GetMapping("/{category}/{postId}")
	public ResponseEntity<PostResponse> getPost(
			@PathVariable CommunityCategoryType category,
			@PathVariable Long postId
	) {
		log.info("게시글 조회 요청: category={}, postId={}", category, postId);
		PostResponse post = postService.getPost(postId, category);
		return ResponseEntity.ok(post);
	}

	// 게시글 작성
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{category}")
	public ResponseEntity<PostResponse> createPost(
			@PathVariable CommunityCategoryType category,
			@Valid @RequestBody PostCreateRequest request,
			Authentication authentication
		) {
		SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
		Long userId = userDetails.getUserId();
		log.info("게시글 작성 요청: category={}, userId={}, title={}", category, userId, request.getTitle());
		PostResponse post = postService.createPost(request, category, userId);
		return ResponseEntity.ok(post);
	}

	// 게시글 수정
	@PreAuthorize("isAuthenticated()")
	@PutMapping("/{category}/{postId}")
	public ResponseEntity<PostResponse> updatePost(
			@PathVariable CommunityCategoryType category,
			@PathVariable Long postId,
			@Valid @RequestBody PostUpdateRequest request,
			Authentication authentication
		) {
		SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
		Long userId = userDetails.getUserId();
		log.info("게시글 수정 요청: category={}, postId={}, userId={}", category, postId, userId);
		PostResponse post = postService.updatePost(postId, category, request, userId);
		return ResponseEntity.ok(post);
	}

	// 게시글 삭제
	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{category}/{postId}")
	public ResponseEntity<Void> deletePost(
			@PathVariable CommunityCategoryType category,
			@PathVariable Long postId,
			Authentication authentication
		) {
		SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
		Long userId = userDetails.getUserId();
		log.info("게시글 삭제 요청: category={}, postId={}, userId={}", category, postId, userId);
		postService.deletePost(postId, category, userId);
		return ResponseEntity.ok().build();
	}

	// 게시글 검색
	@GetMapping
	public ResponseEntity<Page<PostResponse>> searchPosts(
			@RequestParam String keyword,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) {
		log.info("게시글 검색 요청: keyword={}, page={}, size={}", keyword, page, size);
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<PostResponse> posts = postService.searchPosts(keyword, pageable);
		return ResponseEntity.ok(posts);
	}

	// 자유게시판 베스트 게시글 조회
	@GetMapping("/free/best")
	public ResponseEntity<List<PostResponse>> getFreeBestPosts(
			@RequestParam(defaultValue = "10") int size
	) {
		log.info("자유게시판 베스트 게시글 조회 요청: size={}", size);
		Pageable pageable = PageRequest.of(0, size);
		List<PostResponse> posts = postService.getBestPostsByCategory(CommunityCategoryType.FREE, pageable);
		return ResponseEntity.ok(posts);
	}

	// 리뷰 게시판 베스트 게시글 조회
	@GetMapping("/review/best")
	public ResponseEntity<List<PostResponse>> getReviewBestPosts(
			@RequestParam(defaultValue = "10") int size
	) {
		log.info("리뷰 게시판 베스트 게시글 조회 요청: size={}", size);
		Pageable pageable = PageRequest.of(0, size);
		List<PostResponse> posts = postService.getBestPostsByCategory(CommunityCategoryType.REVIEW, pageable);
		return ResponseEntity.ok(posts);
	}

	// 게시글 좋아요
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{postId}/like")
	public ResponseEntity<PostResponse> likePost(
			@PathVariable Long postId,
			Authentication authentication
		) {
		SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
		Long userId = userDetails.getUserId();
		log.info("게시글 좋아요 요청: postId={}, userId={}", postId, userId);
		PostResponse post = postService.likePost(postId, userId);
		return ResponseEntity.ok(post);
	}

	// 게시글 좋아요 취소
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{postId}/dislike")
	public ResponseEntity<PostResponse> dislikePost(
			@PathVariable Long postId,
			Authentication authentication
		) {
		SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
		Long userId = userDetails.getUserId();
		log.info("게시글 좋아요 취소 요청: postId={}, userId={}", postId, userId);
		PostResponse post = postService.dislikePost(postId, userId);
		return ResponseEntity.ok(post);
	}
}
