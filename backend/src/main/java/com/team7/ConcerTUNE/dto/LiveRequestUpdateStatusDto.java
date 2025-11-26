package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.RequestStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiveRequestUpdateStatusDto {
    private RequestStatus status;
    private String rejectionReason;
}