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

    // JPQL 프로젝션을 위한 생성자 추가
    // COUNT(ua)는 Long 타입으로 반환되므로 이를 처리
    public ArtistSummaryDto(Long artistId, String artistName, String artistImageUrl, Long followerCount) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.artistImageUrl = artistImageUrl;
        this.followerCount = followerCount != null ? followerCount : 0L;
    }

    public static ArtistSummaryDto fromEntity(Artist artist, long followerCount) {
        return ArtistSummaryDto.builder()
                .artistId(artist.getArtistId())
                .artistName(artist.getArtistName())
                .artistImageUrl(artist.getArtistImageUrl())
                .followerCount(followerCount)
                .build();
    }
}
