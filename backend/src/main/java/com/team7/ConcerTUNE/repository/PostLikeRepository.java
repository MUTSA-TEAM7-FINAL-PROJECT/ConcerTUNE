package com.team7.ConcerTUNE.temp.repository;

import com.team7.ConcerTUNE.entity.Post;
import com.team7.ConcerTUNE.entity.PostLike;
import com.team7.ConcerTUNE.entity.PostLikeId;
import com.team7.ConcerTUNE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {

    // 특정 게시글에 대한 특정 사용자의 좋아요 여부 확인
    Optional<PostLike> findByUserAndPost(User user, Post post);

    boolean existsByUserAndPost(User user, Post post);

}
