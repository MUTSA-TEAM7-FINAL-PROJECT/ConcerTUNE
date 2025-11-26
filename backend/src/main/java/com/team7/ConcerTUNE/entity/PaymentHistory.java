package com.team7.ConcerTUNE.temp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment_history")
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private DonationSubscription subscription;

    @Column(nullable = false)
    private int amount;

    @Column(name = "toss_payment_key", nullable = false, length = 200)
    private String tossPaymentKey;

    @Column(name = "order_id", nullable = false, length = 100)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status; // 결제 결과 (SUCCESS, FAIL, CANCELED 등)

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate; // 결제 처리 완료/시도 일시

    @Column(name = "fail_reason", length = 255)
    private String failReason; // 결제 실패 시 사유 (선택 사항)
}