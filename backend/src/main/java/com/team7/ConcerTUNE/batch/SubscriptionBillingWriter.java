package com.team7.ConcerTUNE.batch;

import com.team7.ConcerTUNE.entity.DonationSubscription;
import com.team7.ConcerTUNE.repository.DonationSubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@Slf4j
public class SubscriptionBillingWriter implements ItemWriter<DonationSubscription> {

    private final DonationSubscriptionRepository repository;

    public SubscriptionBillingWriter(DonationSubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void write(Chunk<? extends DonationSubscription> chunk) {
        List<? extends DonationSubscription> items = chunk.getItems();

        repository.saveAll(items);

        log.info("[WRITER] {}개의 구독 결제 처리 및 저장 완료", items.size());
    }
}
