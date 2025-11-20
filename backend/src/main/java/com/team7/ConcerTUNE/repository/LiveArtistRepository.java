package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.ArtistManager;
import com.team7.ConcerTUNE.entity.LiveArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LiveArtistRepository extends JpaRepository<LiveArtist, Long> {
    @Query("""
        SELECT la
        FROM LiveArtist la
        JOIN FETCH la.artist a
        JOIN FETCH la.live l
        JOIN FETCH l.liveSchedules ls
        JOIN FETCH ls.schedule s
        JOIN UserArtist ua ON ua.artist = a
        WHERE ua.user.id = :userId
          AND s.liveDate >= :currentDate
    """)
    List<LiveArtist> findUpcomingLivesByUser(
            @Param("userId") Long userId,
            @Param("currentDate") LocalDate currentDate
    );

    List<LiveArtist> findByArtist_ArtistId(Long artistId);

    List<LiveArtist> findByArtist_ArtistIdIn(List<Long> artistIds);
}
