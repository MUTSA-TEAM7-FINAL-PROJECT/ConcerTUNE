package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.LiveRequest;
import com.team7.ConcerTUNE.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiveRequestRepository extends JpaRepository<LiveRequest, Long> {
    List<LiveRequest> findByRequestStatus(RequestStatus status);
}
