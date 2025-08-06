package com.example.payoutprocessor.config;

import com.example.payoutprocessor.service.WakandaPayoutProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulerConfig {

    private final WakandaPayoutProcessor processor;

    public SchedulerConfig(WakandaPayoutProcessor processor) {
        this.processor = processor;
    }

    @Scheduled(cron = "0 40 3 * * *")
    public void scheduleProcessing() {
        try {
            processor.processFiles();
            log.info("Scheduled payout processing completed successfully");
        } catch (Exception e) {
            log.error("Error processing payout files", e);
        }
    }
}
