package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.Live;
import com.team7.ConcerTUNE.entity.RequestStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveRequestResponse {
    private Long requestId;
    private String title;
    private String description;
    private String posterUrl;
    private String ticketUrl;
    private String venue;
    private Map<String, Integer> seatPrices;
    private List<Long> artistIds;
    private List<String> artistNames;
    private Long requesterId;
    private List<LiveScheduleDto> schedules;
    private String requester;
    private RequestStatus requestStatus;
    private String rejectionReason;
    private LocalDateTime requestCreatedAt;
    private LocalDateTime statusUpdatedAt;
    private List<ArtistSummaryDto> artists;

    private List<NewArtistRequest> newArtistRequestsData;

    public static LiveRequestResponse fromEntity(Live live) {
        return fromEntity(live, List.of());
    }

    public static LiveRequestResponse fromEntity(
            Live live,
            List<NewArtistRequest> newArtistRequestsData
    ) {
        List<Long> artistIds = live.getLiveArtists() == null ? List.of()
                : live.getLiveArtists().stream()
                .map(link -> link.getArtist().getArtistId())
                .toList();

        List<String> artistNames = live.getLiveArtists() == null ? List.of()
                : live.getLiveArtists().stream()
                .map(link -> link.getArtist().getArtistName())
                .toList();

        List<ArtistSummaryDto> artists = live.getLiveArtists() == null ? List.of()
                : live.getLiveArtists().stream()
                .map(link -> ArtistSummaryDto.fromEntity(link.getArtist()))
                .toList();

        List<LiveScheduleDto> schedules = live.getLiveSchedules() == null ? List.of()
                : live.getLiveSchedules().stream()
                .map(ls -> LiveScheduleDto.fromEntity(ls.getSchedule()))
                .toList();

        return LiveRequestResponse.builder()
                .requestId(live.getId())
                .title(live.getTitle())
                .description(live.getDescription())
                .posterUrl(live.getPosterUrl())
                .ticketUrl(live.getTicketUrl())
                .venue(live.getVenue())
                .seatPrices(live.getPrice())
                .artistIds(artistIds)
                .artistNames(artistNames)
                .artists(artists) // 👈 여기
                .requesterId(live.getWriter() != null ? live.getWriter().getId() : null)
                .requester(live.getWriter() != null ? live.getWriter().getUsername() : null)
                .schedules(schedules)
                .requestStatus(live.getRequestStatus())
                .rejectionReason(null)
                .requestCreatedAt(live.getCreatedAt())
                .statusUpdatedAt(live.getUpdatedAt())
                .newArtistRequestsData(newArtistRequestsData)
                .build();
    }

}