package com.team7.ConcerTUNE.service;

import com.team7.ConcerTUNE.dto.*;
import com.team7.ConcerTUNE.entity.*;
import com.team7.ConcerTUNE.repository.LivesRepository;
import com.team7.ConcerTUNE.repository.UserRepository;
import com.team7.ConcerTUNE.event.CommentCreatedEvent;
import com.team7.ConcerTUNE.repository.CommentRepository;
import com.team7.ConcerTUNE.repository.PostLikeRepository;
import com.team7.ConcerTUNE.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {

    private final ApplicationEventPublisher eventPublisher;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final LivesRepository livesRepository;
    private final AuthService authService;
    // **********************************
    // 1. Post (게시글) Logic
    // **********************************

    /**
     * POST /api/posts/{category} - 새 게시글 등록
     */
    @Transactional
    public PostDetailResponse createPost(CommunityCategoryType category, PostCreateRequest request, Long userId) {

        // 1. Live 엔티티 처리: request.getLiveId()가 null이 아닌 경우에만 찾습니다.
        Lives live = null;
        if (request.getLiveId() != null) {
            live = livesRepository.findById(Long.valueOf(request.getLiveId()))
                    .orElseThrow(() -> new IllegalArgumentException("Live not found with id: " + request.getLiveId()));
        }

        // 2. User 엔티티 처리 (필수)
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 3. Post 객체 생성 시 live를 전달 (live는 null일 수 있음)
        Post post = Post.create(category, request.getTitle(), request.getContent(),
                request.getImageUrls(), request.getFileUrls(), writer, live);

        Post savedPost = postRepository.save(post);
        return PostDetailResponse.from(savedPost);
    }

    /**
     * GET /api/posts/{category} - 특정 카테고리 게시판 조회
     */
    public Page<PostListResponse> getPostsByCategory(CommunityCategoryType category, Pageable pageable) {
        Page<Post> posts = postRepository.findByCategory(category, pageable);
        return PostListResponse.fromPage(posts);
    }

    public Page<PostListResponse> getPostsByLiveAndCategory(
            Long liveId,
            CommunityCategoryType category,
            Pageable pageable
    ) {
        Page<Post> posts = postRepository.findByLiveIdAndCategory(liveId, category, pageable);
        return PostListResponse.fromPage(posts);
    }

    /**
     * GET /api/posts/{category}/{postId} - 특정 게시글 조회
     */
    @Transactional
    public PostDetailResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        // 조회수 증가 로직 (Transactional 필요)
        post.incrementViewCount();

        return PostDetailResponse.from(post);
    }

    /**
     * PUT /api/posts/{category}/{postId} - 특정 게시글 수정
     * (권한 체크는 SecurityContextHolder 등에서 userId를 받아와서 처리한다고 가정)
     */
    @Transactional
    public PostDetailResponse updatePost(Long postId, PostUpdateRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        // 작성자 권한 확인 (Mock: 실제 사용자 ID와 작성자 ID 비교)
        if (!post.getWriter().getId().equals(userId)) {
            throw new IllegalAccessError("You do not have permission to modify this post.");
        }

        // Post 엔티티에 update() 메서드가 있다고 가정
        post.update(request.getTitle(), request.getContent(), request.getImageUrls(), request.getFileUrls());

        // JpaRepository의 save() 메서드는 update 역할도 수행하지만, @Transactional 내에서는 Dirty Checking으로 자동 저장됩니다.
        return PostDetailResponse.from(post);
    }

    /**
     * DELETE /api/posts/{category}/{postId} - 특정 게시글 삭제
     */
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        // 작성자 권한 확인
        if (!post.getWriter().getId().equals(userId)) {
            throw new IllegalAccessError("You do not have permission to delete this post.");
        }

        postRepository.delete(post);
    }

    /**
     * GET /api/posts - 게시글 검색
     */
    public Page<PostListResponse> searchPosts(String keyword, Pageable pageable) {
        // 제목과 내용 모두에서 검색
        Page<Post> posts = postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        return PostListResponse.fromPage(posts);
    }

    public List<PostSummaryDto> getTop3WeeklyPosts() {
        // 7일 전 시간 계산
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);

        // 좋아요 수 내림차순, 크기 3개로 제한
        PageRequest pageRequest = PageRequest.of(0, 3); // 정렬은 쿼리에서 처리하므로 여기서는 크기만 지정

        return postRepository.findTop3WeeklyPosts(oneWeekAgo, pageRequest).stream()
                .map(PostSummaryDto::new) // Post 엔티티를 PostSummaryDto로 변환하는 생성자가 있다고 가정
                .collect(Collectors.toList());
    }

    public List<BookmarkedPostDto> getBookmarkedConcertPosts(Authentication authentication) {
        User user = authService.getUserFromAuth(authentication);

        List<Object[]> rawPosts = postRepository.findBookmarkedPostsRawData(user.getId());

        List<BookmarkedPostDto> posts = rawPosts.stream()
                .map(row -> {
                    return new BookmarkedPostDto(
                            (Long) row[0],
                            (String) row[1],
                            (String) row[2],
                            ((Number) row[3]).longValue(),
                            (String) row[4]
                    );
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());

        return posts;
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
            post.decrementLikeCount();
            return false; // 좋아요 취소됨
        } else {
            // 좋아요를 누르지 않았다면 -> 좋아요 등록
            PostLike newLike = PostLike.builder().user(user).post(post).build();
            postLikeRepository.save(newLike);
            post.incrementLikeCount();
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

    // **********************************
    // 3. Comment (댓글)
    // **********************************

    public List<CommentResponse> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtAsc(post);
        return CommentResponse.fromList(comments);
    }

    /**
     * POST /api/posts/{postId}/comments - 새 댓글 등록
     */
    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest request, Long userId) {
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        // Comment 엔티티에 create() 메서드가 있다고 가정
        Comment comment = Comment.create(request.getContent(), post, writer);

        Comment savedComment = commentRepository.save(comment);

        // 게시글 댓글 수 증가
        post.incrementCommentCount();
        eventPublisher.publishEvent(new CommentCreatedEvent(this, post, comment.getWriter()));

        return CommentResponse.from(savedComment);
    }

    /**
     * PUT /api/comments/{commentId} - 댓글 수정
     */
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest request, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

        // 작성자 권한 확인
        if (!comment.getWriter().getId().equals(userId)) {
            throw new IllegalAccessError("You do not have permission to modify this comment.");
        }

        // Comment 엔티티에 update() 메서드가 있다고 가정
        comment.update(request.getContent());

        return CommentResponse.from(comment);
    }

    /**
     * DELETE /api/comments/{commentId} - 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

        // 작성자 권한 확인
        if (!comment.getWriter().getId().equals(userId)) {
            throw new IllegalAccessError("You do not have permission to delete this comment.");
        }

        Post post = comment.getPost();
        commentRepository.delete(comment);

        // 게시글 댓글 수 감소
        post.decrementCommentCount();
    }
}