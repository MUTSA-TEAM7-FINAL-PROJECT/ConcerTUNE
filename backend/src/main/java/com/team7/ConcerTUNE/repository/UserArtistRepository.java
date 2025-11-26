package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface UserArtistRepository extends JpaRepository<UserArtist, UserArtistId> {
    // 유저가 특정 아티스트 팔로우했는지 확인
    boolean existsByUserAndArtist(User user, Artist artist);

    // 팔로우 관계 조회
    Optional<UserArtist> findByUserAndArtist(User user, Artist artist);

    // 특정 아티스트를 팔로우하는 모든 UserArtist 관계 조회 (팔로워 목록)
    List<UserArtist> findAllByArtist(Artist artist);

    // 특정 아티스트의 팔로워 수 카운트
    long countByArtist(Artist artist);

    @Query("""
      select ls
      from UserArtist ua
      join ua.artist a
      join LiveArtist la on la.artist = a
      join la.live l
      join l.liveSchedules ls
      join ls.schedule s
      where ua.user = :user
        and l.requestStatus = :status
        and (
              s.liveDate > :today
              or (s.liveDate = :today and s.liveTime >= :now)
            )
      order by s.liveDate asc, s.liveTime asc
      """)
    List<LiveSchedule> findFutureSchedulesOfFollowedArtists(
            @Param("user") User user,
            @Param("status") RequestStatus status,
            @Param("today") LocalDate today,
            @Param("now") LocalTime now
    );
}
