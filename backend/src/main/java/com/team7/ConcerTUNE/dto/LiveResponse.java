package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // [추가] Collectors 임포트

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveResponse {

    private Long liveId;
    private String title;
    private String description;
    private String posterUrl;
    private String ticketUrl;
    private String venue;
    private Map<String, Integer> price;
    private String writerName;
    private Long writerId;
    private List<ArtistSummaryDto> artists;
    private List<LiveScheduleDto> schedules;
    private LocalDateTime ticketDateTime;
    private RequestStatus requestStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int countBookmark;
    private Boolean isBookmarked;

    public static LiveResponse fromEntity(Live entity) {

        LiveResponse liveResponse = LiveResponse.builder()
                .liveId(entity.getId()) // [수정] .id() -> .liveId() (필드명 일치)
                .title(entity.getTitle())
                .description(entity.getDescription())
                .posterUrl(entity.getPosterUrl())
                .ticketUrl(entity.getTicketUrl())
                .ticketDateTime(entity.getTicketDateTime())
                .venue(entity.getVenue())
                .price(entity.getPrice())
                .writerName(entity.getWriter().getUsername())
                .writerId(entity.getWriter().getId())
                .artists(
                        entity.getLiveArtists().stream()
                                .map(liveArtist ->
                                        // [수정] 인자 개수 맞춤 (followerCount 자리에 0L 전달)
                                        ArtistSummaryDto.fromEntity(liveArtist.getArtist(), 0L))
                                .collect(Collectors.toList())
                )
                .schedules(
                        entity.getLiveSchedules().stream()
                                .map(liveSchedule ->
                                        // [수정] ScheduleDto -> LiveScheduleDto (올바른 클래스 사용)
                                        LiveScheduleDto.fromEntity(liveSchedule.getSchedule())
                                )
                                .collect(Collectors.toList())
                )
                .requestStatus(entity.getRequestStatus())
                .countBookmark(0)
                .build();

        if (entity.getBookmarks() != null) {
            liveResponse.setCountBookmark(entity.getBookmarks().size());
        }

        return liveResponse;
    }
}