package com.team7.ConcerTUNE.temp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FollowStatusResponse {
    Long artistId;
    boolean isFollowing ;
}
