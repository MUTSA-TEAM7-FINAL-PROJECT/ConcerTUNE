package com.team7.ConcerTUNE.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    private String username;
    private String bio;
    private String phoneNum;
    private List<GenrePreferenceRequest> genrePreferences;
}
