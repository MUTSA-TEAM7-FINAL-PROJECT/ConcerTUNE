package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.ArtistManagerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistManagerRequestRepository extends JpaRepository<ArtistManagerRequest, Long> {
    Page<ArtistManagerRequest> findByUserId(Long userId, Pageable pageable);
}