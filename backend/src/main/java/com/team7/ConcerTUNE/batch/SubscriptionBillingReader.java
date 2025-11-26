package com.team7.ConcerTUNE.temp.batch;

import com.team7.ConcerTUNE.temp.entity.DonationSubscription;
import com.team7.ConcerTUNE.temp.entity.SubscriptionStatus;
import com.team7.ConcerTUNE.temp.repository.DonationSubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;

import java.time.LocalDateTime;
import java.util.Iterator;

@Slf4j
public class SubscriptionBillingReader implements ItemReader<DonationSubscription> {

    private final DonationSubscriptionRepository repository;
    private Iterator<DonationSubscription> iterator;

    public SubscriptionBillingReader(DonationSubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public DonationSubscription read() {
        if (iterator == null) {
            var list = repository.findByStatusAndNextPaymentDateLessThanEqual(
                    SubscriptionStatus.ACTIVE,
                    LocalDateTime.now()
            );
            iterator = list.iterator();

            log.info("[READER] 결제 대상 {}명 조회됨", list.size());
        }

        return iterator.hasNext() ? iterator.next() : null;
    }
}
