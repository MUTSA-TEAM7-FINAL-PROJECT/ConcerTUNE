package com.team7.ConcerTUNE.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GenrePreferenceRequest {
    private Long genreId;
    private String genreName;
    private String type;
}

