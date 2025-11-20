package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.Lives;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LivesRepository extends JpaRepository<Lives, Long> {
    @Query("SELECT l FROM Lives l JOIN l.liveArtists la WHERE la.artist.artistId = :artistId ORDER BY l.id DESC")
    List<Lives> findLivesByArtistId(@Param("artistId") Long artistId);

    Page<Lives> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
