package com.team7.ConcerTUNE.temp.dto;

import com.team7.ConcerTUNE.entity.Schedules;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class ScheduleResponse {
    private Long scheduleId;
    private LocalDate liveDate;
    private LocalTime liveTime;

    public static ScheduleResponse fromEntity(Schedules schedule) {
        return ScheduleResponse.builder()
                .scheduleId(schedule.getId())
                .liveDate(schedule.getLiveDate())
                .liveTime(schedule.getLiveTime())
                .build();
    }
}