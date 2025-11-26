package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // [추가] 특정 날짜의 스케줄 조회 (내일 공연 조회용)
    List<Schedule> findAllByLiveDate(LocalDate liveDate);
}
