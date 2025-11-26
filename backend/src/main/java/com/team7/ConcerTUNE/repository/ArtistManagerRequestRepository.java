package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.ArtistManagerRequest;
import com.team7.ConcerTUNE.entity.RequestStatus;
import com.team7.ConcerTUNE.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistManagerRequestRepository extends JpaRepository<ArtistManagerRequest, Long> {

    Page<ArtistManagerRequest> findAllByStatus(RequestStatus status, Pageable pageable);
    Page<ArtistManagerRequest> findAll(Pageable pageable);
    Page<ArtistManagerRequest> findAllByUser(User user, Pageable pageable);
    List<ArtistManagerRequest> findAllByUserAndStatus(User user, RequestStatus status);
}