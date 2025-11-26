package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.Live;
import com.team7.ConcerTUNE.entity.LiveArtist;
import com.team7.ConcerTUNE.entity.LiveSchedule;
import com.team7.ConcerTUNE.entity.RequestStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LiveScheduleRepository extends JpaRepository<LiveSchedule, Long> {
    void deleteByLive(Live live);

    // 오늘 이후 '승인된 공연' 스케줄들
    List<LiveSchedule> findByLive_RequestStatusAndSchedule_LiveDateGreaterThanEqualOrderBySchedule_LiveDateAscSchedule_LiveTimeAsc(
            RequestStatus status,
            LocalDate from,
            PageRequest pageRequest
    );

    // 기간 내 스케줄들
    List<LiveSchedule> findByLive_RequestStatusAndSchedule_LiveDateBetweenOrderBySchedule_LiveDateAscSchedule_LiveTimeAsc(
            RequestStatus status,
            LocalDate startDate,
            LocalDate endDate
    );
}
