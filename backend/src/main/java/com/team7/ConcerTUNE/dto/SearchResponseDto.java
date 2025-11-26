package com.team7.ConcerTUNE.temp.dto;
import com.team7.ConcerTUNE.entity.Artist;
import com.team7.ConcerTUNE.entity.Lives;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchResponseDto {

    public enum SearchType { LIVE, ARTIST }

    private SearchType type;
    private Long id;
    private String title;
    private String imageUrl;
    private String subInfo;

    // Lives 엔티티로부터 변환
    public static SearchResponseDto fromLive(Lives live) {
        return SearchResponseDto.builder()
                .type(SearchType.LIVE)
                .id(live.getId())
                .title(live.getTitle())
                .imageUrl(live.getPosterUrl())
                .subInfo(live.getVenue())
                .build();
    }

    // Artist 엔티티로부터 변환
    public static SearchResponseDto fromArtist(Artist artist) {
        return SearchResponseDto.builder()
                .type(SearchType.ARTIST)
                .id(artist.getArtistId())
                .title(artist.getArtistName())
                .imageUrl(artist.getArtistImageUrl())
                .subInfo(artist.isDomestic() ? "국내" : "해외")
                .build();
    }
}