package com.team7.ConcerTUNE.temp.repository;

import com.team7.ConcerTUNE.entity.Bookmarks;
import com.team7.ConcerTUNE.entity.Lives;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmarks, Long> {

    @Query("SELECT l FROM Lives l " +
            "JOIN Bookmarks b ON l.id = b.live.id " +
            "JOIN l.liveSchedules ls JOIN ls.schedule s " +
            "WHERE b.user.id = :userId AND s.liveDate >= :now " +
            "ORDER BY s.liveDate ASC, s.liveTime ASC")
    List<Lives> findNearestBookmarkedLive(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}