package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.PostLike;
import com.team7.ConcerTUNE.entity.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// 게시글 좋아요 Repository
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {

	Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);
	long countByPostId(Long postId);
	boolean existsByUserIdAndPostId(Long userId, Long postId);
}
