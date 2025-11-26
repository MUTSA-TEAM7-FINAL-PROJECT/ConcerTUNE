package com.team7.ConcerTUNE.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SubscriptionCreateRequest {
    private int amount;
    private String orderId;
    private String authKey;
}