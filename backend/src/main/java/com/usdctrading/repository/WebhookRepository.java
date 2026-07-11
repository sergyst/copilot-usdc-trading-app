package com.usdctrading.repository;

import com.usdctrading.entity.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface WebhookRepository extends JpaRepository<Webhook, Long> {
    Optional<Webhook> findByEventId(String eventId);
    List<Webhook> findByStatus(Webhook.WebhookStatus status);
    List<Webhook> findByStatusAndRetryCountLessThan(Webhook.WebhookStatus status, Integer maxRetries);
}
