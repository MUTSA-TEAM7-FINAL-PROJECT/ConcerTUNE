package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.Comment;
import com.team7.ConcerTUNE.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // GET /api/posts/{postId}/comments - 특정 게시글의 댓글 목록 조회
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

}