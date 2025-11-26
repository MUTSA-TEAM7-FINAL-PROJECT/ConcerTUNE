package com.team7.ConcerTUNE.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateArtistRequest {
    private String artistName;
    private String snsUrl;
    private List<GenreDto> genres; // genreId + genreName
}