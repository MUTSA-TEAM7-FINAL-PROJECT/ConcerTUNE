package com.team7.ConcerTUNE.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class LiveDto {
    private Long id;
    private String title;
    private String description;
    private String posterUrl;
    private String ticketUrl;
    private String venue;
    private Map<String, Integer> seatPrices;
    private List<LiveArtistDto> liveArtists;
}
