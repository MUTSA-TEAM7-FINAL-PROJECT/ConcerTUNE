package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.CommunityCategoryType;
import com.team7.ConcerTUNE.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
// 게시글 Repository
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	Optional<Post> findTopByCategoryOrderByLikeCountDesc(CommunityCategoryType category);

	@EntityGraph(attributePaths = {"writer"})
	@Query("SELECT p FROM Post p WHERE p.category = :category ORDER BY p.createdAt DESC")
	Page<Post> findByCategory(@Param("category") CommunityCategoryType category, Pageable pageable);

	@EntityGraph(attributePaths = {"writer"})
	@Query("SELECT p FROM Post p WHERE p.id = :postId")
	Optional<Post> findByIdWithWriter(@Param("postId") Long postId);

	@EntityGraph(attributePaths = {"writer"})
	@Query("SELECT p FROM Post p WHERE p.category = :category AND p.title LIKE %:keyword% ORDER BY p.createdAt DESC")
	Page<Post> findByCategoryAndTitleContaining(
			@Param("category") CommunityCategoryType category,
			@Param("keyword") String keyword,
			Pageable pageable
	);

	@EntityGraph(attributePaths = {"writer"})
	@Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% ORDER BY p.createdAt DESC")
	Page<Post> findByTitleContainingOrContentContaining(
			@Param("keyword") String keyword,
			Pageable pageable
	);

	@EntityGraph(attributePaths = {"writer"})
	@Query("SELECT p FROM Post p WHERE p.category = :category ORDER BY p.likeCount DESC, p.createdAt DESC")
	List<Post> findBestPostsByCategory(@Param("category") CommunityCategoryType category, Pageable pageable);

	@EntityGraph(attributePaths = {"writer"})
	@Query("SELECT p FROM Post p WHERE p.writer.id IN :followingIds ORDER BY p.createdAt DESC")
	Page<Post> findByWriterIdIn(@Param("followingIds") List<Long> followingIds, Pageable pageable);

	@EntityGraph(attributePaths = {"writer"})
	@Query("SELECT p FROM Post p WHERE p.writer.id = :userId ORDER BY p.createdAt DESC")
	Page<Post> findByWriterId(@Param("userId") Long userId, Pageable pageable);
}
