package com.team7.ConcerTUNE.temp.dto;

import com.team7.ConcerTUNE.entity.Artist;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArtistResponseDto {
    private Long artistId;
    private String artistName;

    // Entity를 DTO로 변환하는 팩토리 메소드
    public static ArtistResponseDto fromEntity(Artist artist) {
        return ArtistResponseDto.builder()
                .artistId(artist.getArtistId())
                .artistName(artist.getArtistName())
                .build();
    }
}
