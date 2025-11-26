package com.team7.ConcerTUNE.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LiveArtistUpdateDto {
    private List<Long> artistIds;
}