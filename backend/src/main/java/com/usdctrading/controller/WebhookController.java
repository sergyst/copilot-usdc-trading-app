package com.usdctrading.controller;

import com.usdctrading.dto.circle.CircleWebhookPayload;
import com.usdctrading.entity.Webhook;
import com.usdctrading.service.WebhookService;
import com.usdctrading.util.WebhookSignatureValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/webhooks")
@CrossOrigin(origins = "*")
public class WebhookController {

    @Autowired
    private WebhookService webhookService;

    @Value("${circle.webhook-secret:your_webhook_secret}")
    private String webhookSecret;

    /**
     * Receive webhook from Circle API
     * POST /webhooks/circle
     *
     * Circle sends webhooks to this endpoint to notify about:
     * - Payment status changes (pending, confirmed, failed)
     * - Transfer status changes (pending, complete, failed)
     * - Wallet creation/updates
     *
     * Headers from Circle:
     * - X-Circle-Signature: HMAC-SHA256 signature of payload
     */
    @PostMapping("/circle")
    public ResponseEntity<String> receiveCircleWebhook(
            @Valid @RequestBody CircleWebhookPayload payload,
            @RequestHeader(value = "X-Circle-Signature", required = false) String signature,
            @RequestHeader(value = "X-Circle-Request-Body", required = false) String requestBody) {
        log.info("Received Circle webhook: type={}, eventId={}",
                payload.getType(), payload.getId());

        try {
            // Validate webhook signature if secret is configured
            if (signature != null && !webhookSecret.equals("your_webhook_secret")) {
                String payloadToSign = requestBody != null ? requestBody : payload.toString();
                if (!WebhookSignatureValidator.validateSignature(payloadToSign, signature, webhookSecret)) {
                    log.error("Webhook signature validation failed for eventId: {}", payload.getId());
                    return ResponseEntity.status(401)
                            .body("{\"status\":\"unauthorized\",\"message\":\"Invalid signature\"}");
                }
                log.debug("Webhook signature validated successfully");
            }

            // Process webhook asynchronously
            webhookService.processWebhook(payload);
            log.info("Webhook processed successfully: {}", payload.getId());
            return ResponseEntity.ok("{\"status\":\"received\"}");
        } catch (Exception e) {
            log.error("Error processing webhook: {}", payload.getId(), e);
            // Return 200 to prevent Circle from retrying, but log the error
            return ResponseEntity.ok("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Get webhook details
     * GET /webhooks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Webhook> getWebhook(@PathVariable Long id) {
        log.info("Fetching webhook: {}", id);
        Webhook webhook = webhookService.getWebhook(id);
        return ResponseEntity.ok(webhook);
    }

    /**
     * Get failed webhooks (for monitoring)
     * GET /webhooks/failed
     */
    @GetMapping("/failed")
    public ResponseEntity<?> getFailedWebhooks() {
        log.info("Fetching failed webhooks");
        return ResponseEntity.ok(webhookService.getFailedWebhooks());
    }

    /**
     * Manually trigger webhook retry
     * POST /webhooks/retry
     */
    @PostMapping("/retry")
    public ResponseEntity<String> retryFailedWebhooks() {
        log.info("Triggering webhook retry process");
        try {
            webhookService.retryFailedWebhooks();
            return ResponseEntity.ok("{\"status\":\"retry_started\"}");
        } catch (Exception e) {
            log.error("Error during webhook retry", e);
            return ResponseEntity.status(500)
                    .body("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}
