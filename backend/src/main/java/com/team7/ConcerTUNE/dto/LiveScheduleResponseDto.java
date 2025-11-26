package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.LiveSchedules;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class LiveScheduleResponseDto {
    private Long liveId;
    private String liveTitle;
    private LocalDate liveDate;
    private LocalTime liveTime;
    private String venue;

    public static LiveScheduleResponseDto of(LiveSchedules liveSchedule) {
        return LiveScheduleResponseDto.builder()
                .liveId(liveSchedule.getLive().getId())
                .liveTitle(liveSchedule.getLive().getTitle())
                .liveDate(liveSchedule.getSchedule().getLiveDate())
                .liveTime(liveSchedule.getSchedule().getLiveTime())
                .venue(liveSchedule.getLive().getVenue())
                .build();
    }
}