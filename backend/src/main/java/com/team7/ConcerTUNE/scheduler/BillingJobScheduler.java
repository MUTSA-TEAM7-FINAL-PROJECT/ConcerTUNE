package com.team7.ConcerTUNE.temp.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job subscriptionBillingJob;

    @Scheduled(cron = "0 0 * * * *")
    public void run() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(subscriptionBillingJob, params);

            log.info("[SCHEDULER] 정기 결제 Batch 실행");
        } catch (Exception e) {
            log.error("[SCHEDULER] Batch 실행 실패 - {}", e.getMessage());
        }
    }
}
