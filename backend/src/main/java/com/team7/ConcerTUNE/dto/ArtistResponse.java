package com.team7.ConcerTUNE.temp.dto;

import com.team7.ConcerTUNE.entity.Artist;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArtistResponse {
    private Long artistId;
    private String name;
    private boolean isDomestic;
    private String snsUrl;
    private String artistImageUrl;
    // 장르 목록은 간결화를 위해 생략하거나, 별도의 DTO 리스트로 포함할 수 있습니다.
    // private List<String> genres;

    public static ArtistResponse fromEntity(Artist artist) {
        return ArtistResponse.builder()
                .artistId(artist.getArtistId())
                .name(artist.getArtistName())
                .isDomestic(artist.isDomestic())
                .snsUrl(artist.getSnsUrl())
                .artistImageUrl(artist.getArtistImageUrl())
                .build();
    }
}