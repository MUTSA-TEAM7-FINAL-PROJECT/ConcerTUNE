package com.team7.ConcerTUNE.temp.repository;

import com.team7.ConcerTUNE.entity.LiveRequest;
import com.team7.ConcerTUNE.entity.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveRequestRepository extends JpaRepository<LiveRequest, Long> {

    // 공연 등록 신청 목록 조회 (ADMIN)
    Page<LiveRequest> findAll(Pageable pageable);

    // 상태별 조회 (선택적)
    Page<LiveRequest> findAllByRequestStatus(RequestStatus status, Pageable pageable);

    Page<LiveRequest> findByRequesterId(Long requesterId, Pageable pageable);}