package com.team7.ConcerTUNE.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TossPaymentTransactionDto {
    private String paymentKey;
    private String orderId;
    private String status;
    private String type;
    private String method;
    private String orderName;
    private Long totalAmount;
    private String requestedAt;
    private String approvedAt;
    private CardInfo card;
    private ReceiptInfo receipt;

    @Data
    @Builder
    public static class CardInfo {
        private String number;
        private String issuerCode;
        private String acquirerCode;
        private Integer installmentPlanMonths;
        private String approveNo;
        private String cardType;
    }

    @Data
    @Builder
    public static class ReceiptInfo {
        private String url;
    }
}