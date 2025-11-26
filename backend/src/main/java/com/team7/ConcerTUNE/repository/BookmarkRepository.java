package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByUserAndLive(User user, Live live);

    @Query("SELECT b.live FROM Bookmark b WHERE b.user = :user")
    Page<Live> findBookmarkedLivesByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.live.id = :liveId")
    int countByLiveId(@Param("liveId") Long liveId);

    void deleteByUserAndLive(User user, Live live);

    @Query("""
      select ls
      from Bookmark b
      join b.live l
      join l.liveSchedules ls
      join ls.schedule s
      where b.user = :user
        and l.requestStatus = :status
        and (
              s.liveDate > :today
              or (s.liveDate = :today and s.liveTime >= :now)
            )
      order by s.liveDate asc, s.liveTime asc
      """)
    List<LiveSchedule> findNearestFutureBookmarkedSchedule(
            @Param("user") User user,
            @Param("status") RequestStatus status,
            @Param("today") LocalDate today,
            @Param("now") LocalTime now,
            Pageable pageable
    );
}
