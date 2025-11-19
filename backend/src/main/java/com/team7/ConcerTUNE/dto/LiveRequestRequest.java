package com.team7.ConcerTUNE.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveRequestRequest {

    private String title;

    private String description;

    private String posterUrl;

    private String ticketUrl;

    private String venue;

    private Map<String, Integer> price;

    private Long userId;

    private List<Long> artistIds;

    private List<ScheduleCreateRequest> schedules;
}
