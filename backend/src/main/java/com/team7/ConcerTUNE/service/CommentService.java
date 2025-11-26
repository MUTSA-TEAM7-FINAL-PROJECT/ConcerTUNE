package com.team7.ConcerTUNE.service;

import com.team7.ConcerTUNE.dto.CommentCreateRequest;
import com.team7.ConcerTUNE.dto.CommentUpdateRequest;
import com.team7.ConcerTUNE.dto.CommentResponse;
import com.team7.ConcerTUNE.entity.Comment;
import com.team7.ConcerTUNE.entity.CommentLike;
import com.team7.ConcerTUNE.entity.Post;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.exception.CommentNotFoundException;
import com.team7.ConcerTUNE.exception.PostNotFoundException;
import com.team7.ConcerTUNE.exception.UnauthorizedException;
import com.team7.ConcerTUNE.repository.CommentLikeRepository;
import com.team7.ConcerTUNE.repository.CommentRepository;
import com.team7.ConcerTUNE.repository.PostRepository;
import com.team7.ConcerTUNE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentService {

	private final CommentRepository commentRepository;
	private final CommentLikeRepository commentLikeRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	// 댓글 작성
	public CommentResponse createComment(Long postId, CommentCreateRequest request, Long userId) {
		log.info("댓글 작성 시작: postId={}, userId={}, parent={}", postId, userId, request.getParentCommentId());
		Post post = postRepository.findByIdWithWriter(postId)
				.orElseThrow(() -> new PostNotFoundException(postId));
		User writer = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

		Comment parentComment = null;
		if (request.getParentCommentId() != null) {
			parentComment = commentRepository.findByIdWithWriter(request.getParentCommentId())
					.orElseThrow(() -> new CommentNotFoundException(request.getParentCommentId()));
			if (!parentComment.getPost().getId().equals(postId)) {
				throw new IllegalArgumentException("같은 게시글의 댓글만 부모로 지정할 수 있습니다.");
			}
		}

		Comment comment = Comment.builder()
				.content(request.getContent())
				.writer(writer)
				.post(post)
				.parentComment(parentComment)
				.build();
		if (parentComment != null) {
			parentComment.getReplies().add(comment);
		}
		post.addComment(comment);
		Comment savedComment = commentRepository.save(comment);
		postRepository.save(post);
		log.info("댓글 작성 완료: commentId={}", savedComment.getId());
		return CommentResponse.from(savedComment);
	}

	// 댓글 수정
	public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, Long userId) {
		log.info("댓글 수정 시작: commentId={}, userId={}", commentId, userId);
		Comment comment = commentRepository.findByIdWithWriter(commentId)
				.orElseThrow(() -> new CommentNotFoundException(commentId));
		if (!comment.getWriter().getId().equals(userId)) {
			throw new UnauthorizedException("본인의 댓글만 수정할 수 있습니다.");
		}
		comment.updateContent(request.getContent());
		Comment updatedComment = commentRepository.save(comment);
		log.info("댓글 수정 완료: commentId={}", updatedComment.getId());
		return CommentResponse.from(updatedComment);
	}

	// 댓글 삭제
	public void deleteComment(Long commentId, Long userId) {
		log.info("댓글 삭제 시작: commentId={}, userId={}", commentId, userId);
		Comment comment = commentRepository.findByIdWithWriter(commentId)
				.orElseThrow(() -> new CommentNotFoundException(commentId));
		if (!comment.getWriter().getId().equals(userId)) {
			throw new UnauthorizedException("본인의 댓글만 삭제할 수 있습니다.");
		}
		Post post = comment.getPost();
		post.removeComment(comment);
		commentRepository.delete(comment);
		postRepository.save(post);
		log.info("댓글 삭제 완료: commentId={}", commentId);
	}

	// 게시글의 댓글 조회
	@Transactional(readOnly = true)
	public List<CommentResponse> getCommentsByPostId(Long postId) {
		log.info("댓글 조회: postId={}", postId);
		if (!postRepository.existsById(postId)) {
			throw new PostNotFoundException(postId);
		}
		List<Comment> topLevelComments = commentRepository.findTopLevelByPostId(postId);
		return topLevelComments.stream()
				.map(CommentResponse::from)
				.collect(Collectors.toList());
	}

	// 댓글 좋아요
	public CommentResponse likeComment(Long commentId, Long userId) {
		log.info("댓글 좋아요: commentId={}, userId={}", commentId, userId);
		Comment comment = commentRepository.findByIdWithWriter(commentId)
				.orElseThrow(() -> new CommentNotFoundException(commentId));
		if (commentLikeRepository.existsByUserIdAndCommentId(userId, commentId)) {
			throw new IllegalArgumentException("이미 좋아요를 누른 댓글입니다.");
		}
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
		CommentLike commentLike = CommentLike.builder()
				.user(user)
				.comment(comment)
				.build();
		commentLike.setEmbeddedId();
		commentLikeRepository.save(commentLike);
		comment.increaseLikeCount();
		commentRepository.save(comment);
		log.info("댓글 좋아요 완료: commentId={}, userId={}", commentId, userId);
		return CommentResponse.from(comment);
	}

	// 댓글 좋아요 취소
	public CommentResponse dislikeComment(Long commentId, Long userId) {
		log.info("댓글 좋아요 취소: commentId={}, userId={}", commentId, userId);
		Comment comment = commentRepository.findByIdWithWriter(commentId)
				.orElseThrow(() -> new CommentNotFoundException(commentId));
		CommentLike commentLike = commentLikeRepository.findByUserIdAndCommentId(userId, commentId)
				.orElseThrow(() -> new IllegalArgumentException("좋아요를 누르지 않은 댓글입니다."));
		commentLikeRepository.delete(commentLike);
		comment.decreaseLikeCount();
		commentRepository.save(comment);
		log.info("댓글 좋아요 취소 완료: commentId={}, userId={}", commentId, userId);
		return CommentResponse.from(comment);
	}

	@Transactional(readOnly = true)
	public boolean isCommentLiked(Long commentId, Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

		return commentLikeRepository.existsByUserAndComment(user, comment);
	}
}
