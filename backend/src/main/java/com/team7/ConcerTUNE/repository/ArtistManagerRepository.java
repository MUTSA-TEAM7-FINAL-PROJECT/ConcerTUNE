package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.ArtistManager;
import com.team7.ConcerTUNE.entity.Lives;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistManagerRepository extends JpaRepository<ArtistManager, Long> {
    Optional<ArtistManager> findByIdUserIdAndIdArtistId(Long userId, Long artistId);
}
