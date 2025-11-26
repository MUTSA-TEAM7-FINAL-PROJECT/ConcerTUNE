package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.PaymentStatus;
import com.team7.ConcerTUNE.entity.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SubscriptionDetailDto {

    // --- 아티스트 정보 ---
    private Long artistId;
    private String artistName;
    private String artistImageUrl;

    // --- 구독 정보 ---
    private LocalDateTime subscribedAt;
    private Integer amount;
    private LocalDateTime nextPaymentDate;
    private SubscriptionStatus status;

    // --- 결제 내역 ---
    private List<PaymentDetail> paymentHistory;

    @Data
    @Builder
    public static class PaymentDetail {
        private String orderId;
        private int amount;
        private PaymentStatus status;
        private LocalDateTime paymentDate;

        // TossPaymentTransaction 상세
        private TossPaymentTransactionDetail tossTransaction;

        @Data
        @Builder
        public static class TossPaymentTransactionDetail {
            private String paymentKey;
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
    }
}
