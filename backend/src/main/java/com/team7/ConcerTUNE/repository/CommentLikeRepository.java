package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.CommentLike;
import com.team7.ConcerTUNE.entity.CommentLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// 댓글 좋아요 Repository
@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {

	Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long commentId);
	long countByCommentId(Long commentId);
	boolean existsByUserIdAndCommentId(Long userId, Long commentId);
}
