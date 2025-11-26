package com.team7.ConcerTUNE.temp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateDto {
    private String username;
    private String bio;
    private List<Long> genreIds;

}
