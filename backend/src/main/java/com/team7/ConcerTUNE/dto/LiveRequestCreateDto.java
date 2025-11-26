package com.team7.ConcerTUNE.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class LiveRequestCreateDto {
    private String title;
    private String description;
    private String posterUrl;
    private String ticketUrl;
    private String venue;
    private Map<String, Integer> seatPrices;

    private List<ScheduleDto> schedules;

    private List<Long> existingArtistIds;

    private List<NewArtistRequestDto> newArtistRequests;
}