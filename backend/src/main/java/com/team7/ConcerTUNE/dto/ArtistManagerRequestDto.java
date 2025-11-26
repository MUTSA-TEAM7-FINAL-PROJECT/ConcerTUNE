package com.team7.ConcerTUNE.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistManagerRequestDto {
    private Long artistId;

    @NotBlank(message = "아티스트 이름은 필수입니다.")
    private String artistName;

    @NotBlank(message = "소개글을 입력해주세요.")
    private String description;

    // [중요] 증빙 URL 필수 및 형식 검증
    @NotBlank(message = "증빙 자료(포트폴리오, 버스킹 허가증 등) 링크는 필수입니다.")
    @URL(message = "올바른 URL 형식을 입력해주세요.")
    private String proofDocumentUrl;
}