package com.team7.ConcerTUNE.temp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class FollowArtistFeedDto {

    private Long liveId;
    private String liveTitle;
    private String artistName;
    private LocalDate scheduleDate;
    private String posterUrl; // 포스터 URL 필드
    public FollowArtistFeedDto(Long liveId, String liveTitle, String artistName, LocalDate scheduleDate, String posterUrl) {
        this.liveId = liveId;
        this.liveTitle = liveTitle;
        this.artistName = artistName;
        this.scheduleDate = scheduleDate;
        this.posterUrl = posterUrl;
    }
}