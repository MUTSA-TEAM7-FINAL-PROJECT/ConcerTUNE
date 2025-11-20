package com.team7.ConcerTUNE.temp.repository;

import com.team7.ConcerTUNE.entity.CommunityCategoryType;
import com.team7.ConcerTUNE.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByCategory(CommunityCategoryType category, Pageable pageable);

    Page<Post> findByLiveIdAndCategory(Long liveId, CommunityCategoryType category, Pageable pageable);

    Page<Post> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "WHERE p.createdAt >= :oneWeekAgo " +
            "ORDER BY p.likeCount DESC, p.createdAt DESC")
    List<Post> findTop3WeeklyPosts(LocalDateTime oneWeekAgo, Pageable pageable);

    @Query("SELECT p.id, p.title, w.username, COUNT(pl_all), l.title " +
            "FROM Bookmarks b " +
            "JOIN b.live l " +
            "JOIN Post p ON p.live = l " +
            "JOIN p.writer w " +
            "LEFT JOIN p.likes pl_all " +
            "WHERE b.user.id = :userId " +
            "GROUP BY p.id, p.title, w.username, p.createdAt, l.title " +
            "ORDER BY p.createdAt DESC")
    List<Object[]> findBookmarkedPostsRawData(@Param("userId") Long userId) ;

    List<Post> findByWriterId(Long userId);

}