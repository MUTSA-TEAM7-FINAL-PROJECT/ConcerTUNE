package com.team7.ConcerTUNE.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TossPaymentResponse {

    @JsonProperty("paymentKey")
    private String paymentKey;

    @JsonProperty("method")
    private String method;

    @JsonProperty("type")
    private String type;

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("orderName")
    private String orderName;

    @JsonProperty("status")
    private String status;

    @JsonProperty("totalAmount")
    private Integer totalAmount;

    @JsonProperty("approvedAt")
    private String approvedAt;

    @JsonProperty("requestedAt")
    private String requestedAt;

    @JsonProperty("mId")
    private String mId;

    @JsonProperty("card")
    private Card card;

    @JsonProperty("receipt")
    private Receipt receipt;

    @JsonProperty("failure")
    private Failure failure;


    @Data
    public static class Card {
        @JsonProperty("number")
        private String number; // 카드 번호 (마스킹됨)
        @JsonProperty("issuerCode")
        private String issuerCode;
        @JsonProperty("acquirerCode")
        private String acquirerCode;
        @JsonProperty("installmentPlanMonths")
        private Integer installmentPlanMonths;
        @JsonProperty("approveNo")
        private String approveNo; // 승인 번호
        @JsonProperty("cardType")
        private String cardType; // 카드 종류 (신용/체크)
    }


    @Data
    public static class Receipt {
        @JsonProperty("url")
        private String url;
    }

    @Data
    public static class Failure {
        @JsonProperty("code")
        private String code;

        @JsonProperty("message")
        private String message;
    }
}