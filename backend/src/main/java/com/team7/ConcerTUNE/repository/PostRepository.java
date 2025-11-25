package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.CommunityCategoryType;
import com.team7.ConcerTUNE.entity.Post;
import com.team7.ConcerTUNE.entity.RequestStatus;
import com.team7.ConcerTUNE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
      select p
      from Bookmark b
      join b.live l
      join Post p on p.live = l
      where b.user = :user
        and l.requestStatus = :status
        and p.category = :category
      order by p.createdAt desc
      """)
    List<Post> findReviewPostsForBookmarkedLives(
            @Param("user") User user,
            @Param("status") RequestStatus status,
            @Param("category") CommunityCategoryType category
    );
}

