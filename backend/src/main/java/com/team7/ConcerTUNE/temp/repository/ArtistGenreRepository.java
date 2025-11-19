package com.team7.ConcerTUNE.temp.repository;

import com.team7.ConcerTUNE.entity.ArtistGenre;
import com.team7.ConcerTUNE.entity.LiveRequest;
import com.team7.ConcerTUNE.entity.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistGenreRepository extends JpaRepository<ArtistGenre, Long> {

}