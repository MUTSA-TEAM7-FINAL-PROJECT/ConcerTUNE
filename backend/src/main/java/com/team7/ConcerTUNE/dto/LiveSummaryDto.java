package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.LiveArtist;
import com.team7.ConcerTUNE.entity.Lives;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class LiveSummaryDto {

    private Long liveId;
    private String title;
    private String venue;
    private String posterUrl;
    private String artistNames; // 모든 아티스트 이름을 쉼표로 구분된 문자열로 저장

    public LiveSummaryDto(Lives live) {
        this.liveId = live.getId();
        this.title = live.getTitle();
        this.venue = live.getVenue();
        this.posterUrl = live.getPosterUrl();

        if (live.getLiveArtists() != null && !live.getLiveArtists().isEmpty()) {
            this.artistNames = live.getLiveArtists().stream()
                    .map(LiveArtist::getArtist)
                    .map(artist -> artist != null ? artist.getArtistName() : "이름 없음")
                    .collect(Collectors.joining(", "));
        } else {
            this.artistNames = "정보 없음";
        }
    }
}