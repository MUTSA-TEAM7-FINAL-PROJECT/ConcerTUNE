package com.team7.ConcerTUNE.temp.dto;

import com.team7.ConcerTUNE.entity.RequestStatus;
import com.team7.ConcerTUNE.temp.entity.ArtistManagerRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ArtistManagerRequestResponse {

    private Long requestId;
    private Long userId;
    private String username;
    private Long artistId;
    private String artistName;
    private boolean isOfficial;
    private RequestStatus status;
    private String reason;
    private String adminNote;
    private LocalDateTime requestedAt;

    public static ArtistManagerRequestResponse from(ArtistManagerRequest request) {

        return ArtistManagerRequestResponse.builder()
                .requestId(request.getId())
                .userId(request.getUser().getId())
                .username(request.getUser().getUsername())
                .artistId(request.getArtist().getArtistId())
                .artistName(request.getArtist().getArtistName())
                .isOfficial(request.isOfficial())
                .status(request.getStatus())
                .reason(request.getReason())
                .adminNote(request.getAdminNote())
                .requestedAt(request.getCreatedAt())
                .build();
    }
}