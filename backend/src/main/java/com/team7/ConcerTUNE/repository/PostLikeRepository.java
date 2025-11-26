package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.Post;
import com.team7.ConcerTUNE.entity.PostLike;
import com.team7.ConcerTUNE.entity.PostLikeId;
import com.team7.ConcerTUNE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// 게시글 좋아요 Repository
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {

	Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);
	long countByPostId(Long postId);
	boolean existsByUserIdAndPostId(Long userId, Long postId);

	Optional<PostLike> findByUserAndPost(User user, Post post);

	boolean existsByUserAndPost(User user, Post post);

}
