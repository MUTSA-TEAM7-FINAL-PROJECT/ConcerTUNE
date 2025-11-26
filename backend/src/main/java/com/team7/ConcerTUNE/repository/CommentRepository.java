package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// 댓글 Repository
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	@EntityGraph(attributePaths = {"writer", "post"})
	@Query("SELECT c FROM Comment c WHERE c.id = :commentId")
	Optional<Comment> findByIdWithWriter(@Param("commentId") Long commentId);

	@EntityGraph(attributePaths = {"writer", "post"})
	@Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parentComment IS NULL ORDER BY c.createdAt ASC")
	List<Comment> findTopLevelByPostId(@Param("postId") Long postId);

	long countByPostId(Long postId);
}
