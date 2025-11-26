package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.Bookmarks;
import com.team7.ConcerTUNE.entity.Lives;
import com.team7.ConcerTUNE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmarks, Long> {

    boolean existsByLiveIdAndUser(Long liveId, User user);

    Optional<Bookmarks> findByLiveIdAndUser(Long liveId, User user);

    @Query("SELECT l FROM Lives l " +
            "JOIN Bookmarks b ON l.id = b.live.id " +
            "JOIN l.liveSchedules ls JOIN ls.schedule s " +
            "WHERE b.user.id = :userId AND s.liveDate >= :now " +
            "ORDER BY s.liveDate ASC, s.liveTime ASC")
    List<Lives> findNearestBookmarkedLive(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b.live.id FROM Bookmarks b WHERE b.user.id = :userId")
    List<Long> findLiveIdsByUserId(@Param("userId") Long userId);

    List<Bookmarks> findByUserId(Long userId);
}