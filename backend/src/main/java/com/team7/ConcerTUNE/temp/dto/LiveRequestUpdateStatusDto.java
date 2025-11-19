package com.team7.ConcerTUNE.temp.dto;

import com.team7.ConcerTUNE.entity.RequestStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiveRequestUpdateStatusDto {
    private RequestStatus status;
    private String rejectionReason; // 거절 시 사유를 받기 위한 필드 (선택 사항)
}