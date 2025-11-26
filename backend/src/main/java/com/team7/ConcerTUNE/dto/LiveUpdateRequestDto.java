package com.team7.ConcerTUNE.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiveUpdateRequestDto {
    private String title;
    private String description;
    private String posterUrl;
    private String ticketUrl;
    private String venue;
    private Integer price;
}