package com.team7.ConcerTUNE.temp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ArtistManagerRequestCreateDto {

    @NotNull(message = "아티스트 ID는 필수입니다.")
    private Long artistId;

    @NotBlank(message = "요청 이유는 필수입니다.")
    private String reason;

    @NotNull(message = "공식 관계자 여부는 필수입니다.")
    private Boolean isOfficial;
}