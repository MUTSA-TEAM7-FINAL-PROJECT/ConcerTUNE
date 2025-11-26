package com.team7.ConcerTUNE.entity;

import com.team7.ConcerTUNE.dto.TossPaymentResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 사용을 위해 protected 기본 생성자 추가
@EntityListeners(AuditingEntityListener.class)
@Table(name = "toss_payment_transactions")
public class TossPaymentTransaction {

    // 고유 ID (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Core Payment Keys & Status ---

    /** 가맹점 ID (mId) */
    @Column(nullable = false)
    private String mId; // DTO에 없으므로 임시 값 필요

    /** 토스페이먼츠에서 발급하는 고유 결제 키 (Payment Key) */
    @Column(nullable = false, unique = true)
    private String paymentKey;

    /** 가맹점에서 주문 건을 구분하기 위해 발급한 고유 ID */
    @Column(nullable = false)
    private String orderId;

    /** 결제 상태 (DONE, CANCELED 등) */
    @Column(nullable = false)
    private String status;

    /** 결제 유형 (NORMAL: 일반 결제, BILLING: 정기 결제) */
    private String type;

    /** 결제 수단 (카드, 가상계좌 등) */
    private String method;

    // --- Amount & Order Info ---

    /** 주문 상품명 */
    @Column(nullable = false)
    private String orderName;

    /** 최종 결제 금액 (DB에는 Long 타입으로 저장) */
    @Column(nullable = false)
    private Long totalAmount;

    // JSON 응답에 없는 필드들은 DTO 매핑에서 제외하거나 기본값 처리 필요
    /** 취소 가능 잔액 */
    private Long balanceAmount;

    /** 공급가액 (suppliedAmount) */
    private Long suppliedAmount;

    /** 부가세 (vat) */
    private Long vat;

    /** --- Timestamps --- */

    /** 결제 요청 일시 (ISO 8601 원본 문자열) */
    private String requestedAt;

    /** 결제 승인 일시 (ISO 8601 원본 문자열) */
    private String approvedAt;

    /** DB 저장 일시 */
    @CreatedDate
    private LocalDateTime createdAt;

    @Embedded
    private CardInfo card;

    /** 영수증 URL */
    @Embedded
    private ReceiptInfo receipt;

    public static TossPaymentTransaction fromDto(TossPaymentResponse dto) {
        TossPaymentTransaction entity = new TossPaymentTransaction();
        entity.setMId(dto.getMId());
        entity.setPaymentKey(dto.getPaymentKey());
        entity.setOrderId(dto.getOrderId());
        entity.setOrderName(dto.getOrderName());
        entity.setStatus(dto.getStatus());
        entity.setType(dto.getType());
        entity.setMethod(dto.getMethod());

        entity.setTotalAmount(dto.getTotalAmount() != null ? dto.getTotalAmount().longValue() : 0L);
        entity.setRequestedAt(dto.getRequestedAt());
        entity.setApprovedAt(dto.getApprovedAt());

        // 🚀 카드 정보 매핑 로직
        if (dto.getCard() != null) {
            TossPaymentResponse.Card dtoCard = dto.getCard();
            CardInfo entityCardInfo = new CardInfo();
            entityCardInfo.setNumber(dtoCard.getNumber());
            entityCardInfo.setIssuerCode(dtoCard.getIssuerCode());
            entityCardInfo.setAcquirerCode(dtoCard.getAcquirerCode());
            entityCardInfo.setInstallmentPlanMonths(dtoCard.getInstallmentPlanMonths());
            entityCardInfo.setApproveNo(dtoCard.getApproveNo());
            entityCardInfo.setCardType(dtoCard.getCardType());
            entity.setCard(entityCardInfo);
        }

        // 영수증 정보 매핑
        if (dto.getReceipt() != null) {
            ReceiptInfo receiptInfo = new ReceiptInfo();
            receiptInfo.setUrl(dto.getReceipt().getUrl());
            entity.setReceipt(receiptInfo);
        }

        return entity;
    }


    @Getter @Setter @Embeddable @NoArgsConstructor
    public static class CardInfo {
        private String number;
        private String issuerCode;
        private String acquirerCode;
        private Integer installmentPlanMonths;
        private String approveNo;
        private Long amount;
        private String cardType;
    }

    @Getter @Setter @Embeddable @NoArgsConstructor
    public static class ReceiptInfo {
        @Column(columnDefinition = "TEXT")
        private String url;
    }
}