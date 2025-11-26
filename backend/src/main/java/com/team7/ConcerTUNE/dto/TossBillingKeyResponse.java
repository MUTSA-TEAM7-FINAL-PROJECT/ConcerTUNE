package com.team7.ConcerTUNE.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TossBillingKeyResponse {

    @JsonProperty("billingKey")
    private String billingKey;

    @JsonProperty("customerKey")
    private String customerKey;

}