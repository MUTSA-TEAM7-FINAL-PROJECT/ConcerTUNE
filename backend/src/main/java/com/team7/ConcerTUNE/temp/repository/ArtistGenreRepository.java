package com.team7.ConcerTUNE.temp.repository;

import com.team7.ConcerTUNE.entity.ArtistGenre;
import com.team7.ConcerTUNE.entity.LiveRequest;
import com.team7.ConcerTUNE.entity.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ArtistGenreRepository extends JpaRepository<ArtistGenre, Long> {
    @Modifying
    @Query("DELETE FROM ArtistGenre ag WHERE ag.artist.id = :artistId")
    void deleteByArtistId(Long artistId);
}