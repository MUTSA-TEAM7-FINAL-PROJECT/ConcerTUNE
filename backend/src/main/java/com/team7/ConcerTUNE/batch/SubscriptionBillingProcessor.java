package com.team7.ConcerTUNE.temp.batch;

import com.team7.ConcerTUNE.temp.entity.DonationSubscription;
import com.team7.ConcerTUNE.temp.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;

@Slf4j
public class SubscriptionBillingProcessor implements ItemProcessor<DonationSubscription, DonationSubscription> {

    private final SubscriptionService subscriptionService;

    public SubscriptionBillingProcessor(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public DonationSubscription process(DonationSubscription sub) throws Exception {

        log.info("[PROCESSOR] subscriptionId={} 결제 시도", sub.getId());

        subscriptionService.executeBilling(sub);

        // 결제 완료 → 다음 결제일 + 1달
        sub.setNextPaymentDate(sub.getNextPaymentDate().plusMonths(1));

        return sub; // Writer로 전달됨
    }
}
