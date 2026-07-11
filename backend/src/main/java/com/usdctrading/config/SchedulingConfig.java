package com.usdctrading.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.usdctrading.service.WebhookService;

@Slf4j
@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Autowired
    private WebhookService webhookService;

    /**
     * Retry failed webhooks every 5 minutes
     * This ensures that temporary failures are recovered
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void retryFailedWebhooksScheduled() {
        log.debug("Scheduled webhook retry check");
        try {
            webhookService.retryFailedWebhooks();
        } catch (Exception e) {
            log.error("Error in scheduled webhook retry", e);
        }
    }
}
