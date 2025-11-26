package com.team7.ConcerTUNE.temp.dto;

import com.team7.ConcerTUNE.dto.GenreDto;
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