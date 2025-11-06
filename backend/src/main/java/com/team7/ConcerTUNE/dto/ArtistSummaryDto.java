package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.Artist;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistSummaryDto {
    private Long artistId;
    private String artistName;
    private String artistImageUrl;
    private long followerCount;

    public static ArtistSummaryDto fromEntity(Artist artist, long followerCount) {
        return ArtistSummaryDto.builder()
                .artistId(artist.getArtistId())
                .artistName(artist.getArtistName())
                .artistImageUrl(artist.getArtistImageUrl())
                .followerCount(followerCount)
                .build();
    }
}
