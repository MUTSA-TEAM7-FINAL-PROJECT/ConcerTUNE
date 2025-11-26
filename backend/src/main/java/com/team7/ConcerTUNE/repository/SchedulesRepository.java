package com.team7.ConcerTUNE.temp.repository;

import com.team7.ConcerTUNE.entity.Schedules;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface SchedulesRepository extends JpaRepository<Schedules, Long> {
    Optional<Schedules> findByLiveDateAndLiveTime(LocalDate liveDate, LocalTime liveTime);

}