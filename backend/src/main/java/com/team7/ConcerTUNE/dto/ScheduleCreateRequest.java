package com.team7.ConcerTUNE.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Data
public class ScheduleCreateRequest {
    private LocalDate liveDate;
    private LocalTime startTime;
}

