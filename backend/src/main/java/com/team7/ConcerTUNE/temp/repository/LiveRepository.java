package com.team7.ConcerTUNE.temp.repository;

import com.team7.ConcerTUNE.entity.Lives;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface LiveRepository extends JpaRepository<Lives, Long> {

    @Query("SELECT DISTINCT l FROM Lives l " +
            "JOIN l.liveArtists la " +
            "JOIN la.artist a " +
            "JOIN a.artistGenres ag " +
            "JOIN ag.genre g " +
            "WHERE g.genreName = :genreName")
    Page<Lives> findLivesByGenreName(@Param("genreName") String genreName, Pageable pageable);

    List<Lives> findTop4ByOrderByCreatedAtDesc(Pageable pageable);

    @Query(value = "SELECT DISTINCT l FROM Lives l " +
            "JOIN FETCH l.liveArtists la " +
            "JOIN FETCH la.artist a " +
            "JOIN FETCH a.artistGenres ag " +
            "JOIN FETCH ag.genre g " +
            "JOIN FETCH l.liveSchedules ls " +
            "JOIN FETCH ls.schedule s " +

            "WHERE a.artistId IN " +
            "    (SELECT DISTINCT art.artistId FROM Artist art JOIN art.artistGenres ag2 WHERE ag2.genre.genreId IN :genreIds) " +
            "AND s.liveDate >= :currentDate " +

            "ORDER BY s.liveDate ASC")
    List<Lives> findPersonalizedRecommendations(
            @Param("genreIds") List<Long> genreIds,
            @Param("currentDate") LocalDate currentDate,
            Pageable pageable);

}