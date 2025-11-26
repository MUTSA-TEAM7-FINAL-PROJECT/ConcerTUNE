package com.team7.ConcerTUNE.batch;

import com.team7.ConcerTUNE.entity.DonationSubscription;
import com.team7.ConcerTUNE.repository.DonationSubscriptionRepository;
import com.team7.ConcerTUNE.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class SubscriptionBillingJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DonationSubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;

    @Bean
    public Job subscriptionBillingJob() {
        return new JobBuilder("subscriptionBillingJob", jobRepository)
                .start(subscriptionBillingStep())
                .build();
    }

    @Bean
    public Step subscriptionBillingStep() {
        return new StepBuilder("subscriptionBillingStep", jobRepository)
                .<DonationSubscription, DonationSubscription>chunk(50, transactionManager)
                .reader(subscriptionBillingReader())
                .processor(subscriptionBillingProcessor())
                .writer(subscriptionBillingWriter())
                .faultTolerant()               // 결제 실패해도 다음 아이템으로 진행
                .skip(Exception.class)         // 실패 시 해당 구독자 스킵
                .skipLimit(100)
                .retry(Exception.class)        // 일시적인 네트워크 문제 시 재시도
                .retryLimit(3)
                .build();
    }

    @Bean
    public ItemReader<DonationSubscription> subscriptionBillingReader() {
        return new SubscriptionBillingReader(subscriptionRepository);
    }

    @Bean
    public ItemProcessor<DonationSubscription, DonationSubscription> subscriptionBillingProcessor() {
        return new SubscriptionBillingProcessor(subscriptionService);
    }

    @Bean
    public ItemWriter<DonationSubscription> subscriptionBillingWriter() {
        return new SubscriptionBillingWriter(subscriptionRepository);
    }
}
