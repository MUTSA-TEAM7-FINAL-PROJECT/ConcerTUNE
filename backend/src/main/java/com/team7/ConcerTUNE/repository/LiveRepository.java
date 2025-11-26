package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.Live;
import com.team7.ConcerTUNE.entity.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LiveRepository extends JpaRepository<Live, Long> {
    Page<Live> findAllByRequestStatus(RequestStatus status, Pageable pageable);

    Optional<Live> findByIdAndRequestStatus(Long id, RequestStatus status);

    Page<Live> findAllByRequestStatusIn(List<RequestStatus> statuses, Pageable pageable);

    Page<Live> findByTitleContainingIgnoreCase(String title, Pageable pageable);

}
