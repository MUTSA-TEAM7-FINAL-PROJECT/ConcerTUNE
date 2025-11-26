package com.team7.ConcerTUNE.temp.dto;

import com.team7.ConcerTUNE.entity.Lives;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
public class LiveResponse {
    private Long liveId;
    private String title;
    private String description;
    private String posterUrl;
    private String ticketUrl;
    private String venue;
    private final Map<String, Integer> seatPrices;
    // 아티스트 목록 (LiveArtist 엔티티를 통해 추출)
    private List<ArtistResponse> artists;
    // 일정 목록 (LiveSchedules 엔티티를 통해 추출)
    private List<ScheduleResponse> schedules;

    public static LiveResponse fromEntity(Lives live) {
        // LiveArtist -> ArtistResponse 변환
        List<ArtistResponse> artistResponses = live.getLiveArtists().stream()
                .map(liveArtist -> ArtistResponse.fromEntity(liveArtist.getArtist()))
                .collect(Collectors.toList());

        // LiveSchedules -> ScheduleResponse 변환
        List<ScheduleResponse> scheduleResponses = live.getLiveSchedules().stream()
                .map(liveSchedule -> ScheduleResponse.fromEntity(liveSchedule.getSchedule()))
                .collect(Collectors.toList());

        return LiveResponse.builder()
                .liveId(live.getId())
                .title(live.getTitle())
                .description(live.getDescription())
                .posterUrl(live.getPosterUrl())
                .ticketUrl(live.getTicketUrl())
                .venue(live.getVenue())
                .seatPrices(live.getSeatPrices())
                .artists(artistResponses)
                .schedules(scheduleResponses)
                .build();
    }
}