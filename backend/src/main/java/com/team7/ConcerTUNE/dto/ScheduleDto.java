package com.team7.ConcerTUNE.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ScheduleDto {
    private LocalDate liveDate;
    private LocalTime liveTime;
}