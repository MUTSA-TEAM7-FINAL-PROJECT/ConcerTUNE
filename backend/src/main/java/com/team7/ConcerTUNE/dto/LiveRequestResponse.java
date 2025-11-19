package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.Artist;
import com.team7.ConcerTUNE.entity.LiveRequest;
import com.team7.ConcerTUNE.entity.RequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveRequestResponse {

    private Long requestId;

    private String title;

    private String description;

    private String posterUrl;

    private String ticketUrl;

    private String venue;

    private Map<String, Integer> price;

    private String userName;

    private List<String> artistNames;

    private List<ScheduleSummaryDto> schedules;

    private RequestStatus requestStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static LiveRequestResponse fromEntity(LiveRequest entity) {
        return LiveRequestResponse.builder()
                .requestId(entity.getRequestId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .posterUrl(entity.getPosterUrl())
                .ticketUrl(entity.getTicketUrl())
                .venue(entity.getVenue())
                .price(entity.getPrice())
                .userName(entity.getUser().getUsername())

                .artistNames(
                        entity.getLiveRequestArtists().stream()
                                .map(liveRequestArtist -> liveRequestArtist.getArtist().getArtistName())
                                .toList()
                )
                .schedules(
                        entity.getLiveRequestSchedules().stream()
                                .map(liveRequestSchedule ->
                                        ScheduleSummaryDto.fromEntity(liveRequestSchedule.getSchedule())
                                )
                                .toList()
                )

                .requestStatus(entity.getRequestStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static List<LiveRequestResponse> fromEntity(List<LiveRequest> entities) {
        return entities.stream()
                .map(LiveRequestResponse::fromEntity)
                .toList();
    }
}
