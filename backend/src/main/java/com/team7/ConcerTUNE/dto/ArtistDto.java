package com.team7.ConcerTUNE.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArtistDto {
    private Long id;
    private String name;
    private String profileImageUrl;
}