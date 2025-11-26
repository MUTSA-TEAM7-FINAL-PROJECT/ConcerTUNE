package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.RequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArtistManagerRequestStatusUpdateDto {

    @NotNull(message = "처리할 상태(APPROVED 또는 REJECTED)는 필수입니다.")
    private RequestStatus status;

    @NotBlank(message = "처리 메모(관리자 사유)는 필수입니다.")
    private String adminNote;
}