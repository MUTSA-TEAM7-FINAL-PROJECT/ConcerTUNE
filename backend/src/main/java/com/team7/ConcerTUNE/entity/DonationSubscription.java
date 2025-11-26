package com.team7.ConcerTUNE.temp.entity;

import com.team7.ConcerTUNE.temp.dto.SubscriptionCreateRequest;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "donation_subscription")
public class DonationSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "artist_id", nullable = false)
    private Long artistId;

    @Column(nullable = true)
    private Integer amount;

    @Column(name = "billing_key", nullable = true)
    private String billingKey;

    @Column(name = "toss_customer_key", nullable = false, length = 100)
    private String tossCustomerKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status;

    @Column(name = "subscribed_at", nullable = false)
    private LocalDateTime subscribedAt;

    @Column(name = "next_payment_date", nullable = true)
    private LocalDateTime nextPaymentDate;


    public void activate(int amount, String billingKey, LocalDateTime nextPaymentDate) {
        this.amount = amount;
        this.status = SubscriptionStatus.ACTIVE;
        this.subscribedAt = LocalDateTime.now();
        this.nextPaymentDate = nextPaymentDate;
        this.billingKey = billingKey;
    }

    public void deactivate() {
        this.status = SubscriptionStatus.INACTIVE;
    }

    public void updateNextPaymentDate(LocalDateTime nextDate) {
        this.nextPaymentDate = nextDate;
    }
}
