package com.team7.ConcerTUNE.service;

import com.team7.ConcerTUNE.dto.PostCreateRequest;
import com.team7.ConcerTUNE.dto.PostCreateWithLIveIdRequest;
import com.team7.ConcerTUNE.dto.PostUpdateRequest;
import com.team7.ConcerTUNE.dto.PostResponse;
import com.team7.ConcerTUNE.entity.*;
import com.team7.ConcerTUNE.exception.PostNotFoundException;
import com.team7.ConcerTUNE.exception.UnauthorizedException;
import com.team7.ConcerTUNE.repository.LiveRepository;
import com.team7.ConcerTUNE.repository.PostLikeRepository;
import com.team7.ConcerTUNE.repository.PostRepository;
import com.team7.ConcerTUNE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// 게시글 서비스
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class 	PostService {

	private final PostRepository postRepository;
	private final PostLikeRepository postLikeRepository;
	private final UserRepository userRepository;
	private final LiveRepository liveRepository;

	// 게시글 작성
	public PostResponse createPost(PostCreateRequest request, CommunityCategoryType category, Long userId) {
		log.info("게시글 작성 시작: userId={}, category={}, title={}", userId, category, request.getTitle());

		Live live = null;
		if (request.getLiveId() != null) {
			live = liveRepository.findById(Long.valueOf(request.getLiveId()))
					.orElseThrow(() -> new IllegalArgumentException("Live not found with id: " + request.getLiveId()));
		}


		User writer = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
		Post post = Post.builder()
				.title(request.getTitle())
				.content(request.getContent())
				.writer(writer)
				.category(category)
				.imageUrls(request.getImageUrls() != null ? request.getImageUrls() : List.of())
				.fileUrls(request.getFileUrls() != null ? request.getFileUrls() : List.of())
				.live(live)
				.build();

		Post savedPost = postRepository.save(post);
		log.info("게시글 작성 완료: postId={}", savedPost.getId());
		return PostResponse.from(savedPost);
	}

	// 게시글 수정
	public PostResponse updatePost(Long postId, CommunityCategoryType category, PostUpdateRequest request, Long userId) {
		log.info("게시글 수정 시작: postId={}, userId={}", postId, userId);
		Post post = postRepository.findByIdWithWriter(postId)
				.orElseThrow(() -> new PostNotFoundException(postId));
		if (post.getCategory() != category) {
			throw new IllegalArgumentException("카테고리가 일치하지 않습니다.");
		}
		if (!post.getWriter().getId().equals(userId)) {
			throw new UnauthorizedException("본인의 게시글만 수정할 수 있습니다.");
		}
		post.update(
				request.getTitle(),
				request.getContent(),
				request.getImageUrls() != null ? request.getImageUrls() : List.of(),
				request.getFileUrls() != null ? request.getFileUrls() : List.of()
		);
		Post updatedPost = postRepository.save(post);
		log.info("게시글 수정 완료: postId={}", updatedPost.getId());
		return PostResponse.from(updatedPost);
	}

	// 게시글 삭제
	public void deletePost(Long postId, CommunityCategoryType category, Long userId) {
		log.info("게시글 삭제 시작: postId={}, userId={}", postId, userId);
		Post post = postRepository.findByIdWithWriter(postId)
				.orElseThrow(() -> new PostNotFoundException(postId));
		if (post.getCategory() != category) {
			throw new IllegalArgumentException("카테고리가 일치하지 않습니다.");
		}
		if (!post.getWriter().getId().equals(userId)) {
			throw new UnauthorizedException("본인의 게시글만 삭제할 수 있습니다.");
		}
		postRepository.delete(post);
		log.info("게시글 삭제 완료: postId={}", postId);
	}

	// 카테고리별 게시글 조회
	@Transactional(readOnly = true)
	public Page<PostResponse> getPostsByCategory(CommunityCategoryType category, Pageable pageable) {
		log.info("카테고리별 게시글 조회: category={}, page={}, size={}", category, pageable.getPageNumber(), pageable.getPageSize());
		Page<Post> posts = postRepository.findByCategory(category, pageable);
		return posts.map(PostResponse::from);
	}

	public Page<PostResponse> getPostsByLiveAndCategory(Long liveId, CommunityCategoryType category, Pageable pageable
	) {
		Page<Post> posts = postRepository.findByLiveIdAndCategory(liveId, category, pageable);
		return posts.map(PostResponse::from);
	}

	// 특정 게시글 조회
	@Transactional
	public PostResponse getPost(Long postId) {
		log.info("게시글 조회: postId={}", postId);
		Post post = postRepository.findByIdWithWriter(postId)
				.orElseThrow(() -> new PostNotFoundException(postId));

		post.increaseViewCount();
		postRepository.save(post);
		return PostResponse.from(post);
	}

	// 게시글 검색
	@Transactional(readOnly = true)
	public Page<PostResponse> searchPosts(String keyword, Pageable pageable) {
		log.info("게시글 검색: keyword={}, page={}, size={}", keyword, pageable.getPageNumber(), pageable.getPageSize());
		Page<Post> posts = postRepository.findByTitleContainingOrContentContaining(keyword, pageable);
		return posts.map(PostResponse::from);
	}

	// 베스트 게시글 조회
	@Transactional(readOnly = true)
	public List<PostResponse> getBestPostsByCategory(CommunityCategoryType category, Pageable pageable) {
		log.info("베스트 게시글 조회: category={}, limit={}", category, pageable.getPageSize());
		List<Post> posts = postRepository.findBestPostsByCategory(category, pageable);
		return posts.stream()
				.map(PostResponse::from)
				.collect(Collectors.toList());
	}

	// 팔로잉 게시글 조회 
	@Transactional(readOnly = true)
	public Page<PostResponse> getFollowingPosts(Long userId, Pageable pageable) {
		log.info("팔로잉 게시글 조회: userId={}, page={}, size={}", userId, pageable.getPageNumber(), pageable.getPageSize());
		return Page.empty(pageable);
	}

	// 게시글 좋아요
	public PostResponse likePost(Long postId, Long userId) {
		log.info("게시글 좋아요: postId={}, userId={}", postId, userId);
		Post post = postRepository.findByIdWithWriter(postId)
				.orElseThrow(() -> new PostNotFoundException(postId));
		if (postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
			throw new IllegalArgumentException("이미 좋아요를 누른 게시글입니다.");
		}
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
		PostLike postLike = PostLike.builder()
				.user(user)
				.post(post)
				.build();
		postLike.setEmbeddedId();
		postLikeRepository.save(postLike);
		post.increaseLikeCount();
		postRepository.save(post);
		log.info("게시글 좋아요 완료: postId={}, userId={}", postId, userId);
		return PostResponse.from(post);
	}

	// 게시글 좋아요 취소
	public PostResponse dislikePost(Long postId, Long userId) {
		log.info("게시글 좋아요 취소: postId={}, userId={}", postId, userId);
		Post post = postRepository.findByIdWithWriter(postId)
				.orElseThrow(() -> new PostNotFoundException(postId));
		PostLike postLike = postLikeRepository.findByUserIdAndPostId(userId, postId)
				.orElseThrow(() -> new IllegalArgumentException("좋아요를 누르지 않은 게시글입니다."));
		postLikeRepository.delete(postLike);
		post.decreaseLikeCount();
		postRepository.save(post);
		log.info("게시글 좋아요 취소 완료: postId={}, userId={}", postId, userId);
		return PostResponse.from(post);
	}

	public List<PostResponse> getTop3WeeklyPosts() {
		LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);

		PageRequest pageRequest = PageRequest.of(0, 3);

		// 3. Repository 호출 및 결과 스트림 변환
		return postRepository.findTop3WeeklyPosts(oneWeekAgo, pageRequest).stream()
				// Post 엔티티를 PostResponse DTO로 명확하게 변환
				.map(PostResponse::from)
				.collect(Collectors.toList());
	}

	@Transactional
	public boolean togglePostLike(Long postId, Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

		PostLike postLike = postLikeRepository.findByUserAndPost(user, post).orElse(null);

		if (postLike != null) {
			// 이미 좋아요를 눌렀다면 -> 좋아요 취소 (삭제)
			postLikeRepository.delete(postLike);
			post.decreaseLikeCount();
			return false; // 좋아요 취소됨
		} else {
			// 좋아요를 누르지 않았다면 -> 좋아요 등록
			PostLike newLike = PostLike.builder().user(user).post(post).build();
			postLikeRepository.save(newLike);
			post.increaseLikeCount();
			return true; // 좋아요 등록됨
		}
	}

	@Transactional(readOnly = true)
	public boolean isPostLiked(Long postId, Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

		return postLikeRepository.existsByUserAndPost(user, post);
	}

}
