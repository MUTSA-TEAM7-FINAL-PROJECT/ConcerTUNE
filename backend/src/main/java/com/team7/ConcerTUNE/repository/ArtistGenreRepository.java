package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.ArtistGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistGenreRepository extends JpaRepository<ArtistGenre, Long> {
}
