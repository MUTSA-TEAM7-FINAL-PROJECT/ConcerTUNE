package com.team7.ConcerTUNE.temp.dto;

import com.team7.ConcerTUNE.entity.Artist;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ArtistDetailResponse {

    private Long artistId;
    private String artistName;
    private boolean isDomestic;
    private String snsUrl;
    private String artistImageUrl;
    private boolean isOfficial;
    private List<String> genres;
    private boolean isFollowing;
    private Long followerCount;
    private List<LiveInfoResponse> relatedLives;

    @Getter
    @Builder
    public static class LiveInfoResponse {
        private Long liveId;
        private String title;
        private String posterUrl;
        private String venue;
        private List<String> scheduleDates;
    }

    public static ArtistDetailResponse from(Artist artist, boolean isFollowing, Long followerCount, List<LiveInfoResponse> relatedLives) {
        return ArtistDetailResponse.builder()
                .artistId(artist.getArtistId())
                .artistName(artist.getArtistName())
                .isDomestic(artist.isDomestic())
                .snsUrl(artist.getSnsUrl())
                .artistImageUrl(artist.getArtistImageUrl())
                .isOfficial(artist.isOfficial())
                .genres(artist.getArtistGenres().stream()
                        .map(ag -> ag.getGenre().getGenreName())
                        .collect(Collectors.toList()))
                .isFollowing(isFollowing)
                .followerCount(followerCount)
                .relatedLives(relatedLives)
                .build();
    }
}