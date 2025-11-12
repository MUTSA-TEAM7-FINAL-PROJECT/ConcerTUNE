package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.ArtistManager;
import com.team7.ConcerTUNE.entity.LiveArtist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LiveArtistRepository extends JpaRepository<LiveArtist, Long> {
}
