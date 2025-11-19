package com.team7.ConcerTUNE.temp.dto;

import com.team7.ConcerTUNE.entity.LiveRequest;
import com.team7.ConcerTUNE.entity.RequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Getter
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
    private List<String> artistNames; // 💡 이 필드를 설정해야 합니다.
    private Long requesterId;
    private String requester;
    private RequestStatus requestStatus;
    private LocalDateTime requestCreatedAt;
    private LocalDateTime statusUpdatedAt;

    private List<NewArtistRequestDto> newArtistRequestsData;


    public static LiveRequestResponse fromEntity(LiveRequest liveRequest, List<String> artistNames) {

        return LiveRequestResponse.builder()
                .requestId(liveRequest.getRequestId())
                .title(liveRequest.getTitle())
                .description(liveRequest.getDescription())
                .posterUrl(liveRequest.getPosterUrl())
                .ticketUrl(liveRequest.getTicketUrl())
                .venue(liveRequest.getVenue())
                .seatPrices(liveRequest.getSeatPrices())

                .artistIds(liveRequest.getArtistIds())
                .artistNames(artistNames)

                .requesterId(liveRequest.getRequester().getId())
                .requester(liveRequest.getRequester().getUsername())
                .requestStatus(liveRequest.getRequestStatus())
                .requestCreatedAt(liveRequest.getRequestCreatedAt())
                .statusUpdatedAt(liveRequest.getStatusUpdatedAt())

                .newArtistRequestsData(liveRequest.getNewArtistRequestData())

                .build();
    }
}