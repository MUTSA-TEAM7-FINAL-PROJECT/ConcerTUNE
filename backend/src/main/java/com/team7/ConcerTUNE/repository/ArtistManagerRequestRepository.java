package com.team7.ConcerTUNE.temp.repository;

import com.team7.ConcerTUNE.temp.entity.ArtistManagerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistManagerRequestRepository extends JpaRepository<ArtistManagerRequest, Long> {
    Page<ArtistManagerRequest> findByUserId(Long userId, Pageable pageable);
}