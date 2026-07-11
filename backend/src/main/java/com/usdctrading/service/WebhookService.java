package com.usdctrading.service;

import com.usdctrading.dto.circle.CircleWebhookPayload;
import com.usdctrading.entity.Webhook;
import com.usdctrading.entity.Transaction;
import com.usdctrading.entity.Order;
import com.usdctrading.repository.WebhookRepository;
import com.usdctrading.repository.TransactionRepository;
import com.usdctrading.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class WebhookService {

    @Autowired
    private WebhookRepository webhookRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private OrderRepository orderRepository;

    private static final Integer MAX_RETRIES = 3;

    /**
     * Process incoming webhook from Circle API
     */
    @Transactional
    public void processWebhook(CircleWebhookPayload payload) {
        log.info("Processing webhook: type={}, eventId={}, resourceId={}",
                payload.getType(), payload.getId(), payload.getData().getId());

        // Check if webhook already processed (idempotency)
        Optional<Webhook> existingWebhook = webhookRepository.findByEventId(payload.getId());
        if (existingWebhook.isPresent()) {
            log.warn("Webhook already processed: {}", payload.getId());
            return;
        }

        // Create webhook record
        Webhook webhook = Webhook.builder()
                .eventId(payload.getId())
                .eventType(payload.getType())
                .resourceId(payload.getData().getId())
                .status(Webhook.WebhookStatus.PROCESSING)
                .payload(payload.toString())
                .retryCount(0)
                .build();

        try {
            // Route to appropriate handler based on event type
            switch (payload.getType()) {
                case "payments.created":
                case "payments.pending":
                case "payments.confirmed":
                case "payments.failed":
                    handlePaymentEvent(payload);
                    break;

                case "transfers.created":
                case "transfers.pending":
                case "transfers.complete":
                case "transfers.failed":
                    handleTransferEvent(payload);
                    break;

                case "wallets.created":
                    handleWalletEvent(payload);
                    break;

                default:
                    log.warn("Unknown webhook event type: {}", payload.getType());
            }

            webhook.setStatus(Webhook.WebhookStatus.SUCCESS);
            log.info("Webhook processed successfully: {}", payload.getId());

        } catch (Exception e) {
            log.error("Error processing webhook: {}", payload.getId(), e);
            webhook.setStatus(Webhook.WebhookStatus.FAILED);
            webhook.setErrorMessage(e.getMessage());

            // Mark for retry if retries available
            if (webhook.getRetryCount() < MAX_RETRIES) {
                webhook.setStatus(Webhook.WebhookStatus.RETRYING);
            }
        }

        webhookRepository.save(webhook);
    }

    /**
     * Handle payment-related webhook events
     */
    private void handlePaymentEvent(CircleWebhookPayload payload) {
        CircleWebhookPayload.WebhookData data = payload.getData();
        String paymentId = data.getId();
        String status = data.getStatus();

        log.info("Processing payment event: paymentId={}, status={}", paymentId, status);

        // Find associated transaction by Circle payment ID
        // Note: You'll need to store paymentId in Transaction entity
        // For now, we'll search by transaction hash
        List<Transaction> transactions = transactionRepository.findByTransactionHash(paymentId);

        if (transactions.isEmpty()) {
            log.warn("No transaction found for payment: {}", paymentId);
            return;
        }

        Transaction transaction = transactions.get(0);

        switch (status.toLowerCase()) {
            case "confirmed":
                log.info("Payment confirmed for transaction: {}", transaction.getId());
                transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
                break;

            case "pending":
                log.info("Payment pending for transaction: {}", transaction.getId());
                transaction.setStatus(Transaction.TransactionStatus.PROCESSING);
                break;

            case "failed":
                log.warn("Payment failed for transaction: {}", transaction.getId());
                transaction.setStatus(Transaction.TransactionStatus.FAILED);
                if (data.getErrorCode() != null) {
                    transaction.setDescription("Error: " + data.getErrorCode());
                }
                break;
        }

        transactionRepository.save(transaction);
        log.info("Transaction updated: id={}, status={}", transaction.getId(), transaction.getStatus());
    }

    /**
     * Handle transfer-related webhook events
     */
    private void handleTransferEvent(CircleWebhookPayload payload) {
        CircleWebhookPayload.WebhookData data = payload.getData();
        String transferId = data.getId();
        String status = data.getStatus();

        log.info("Processing transfer event: transferId={}, status={}", transferId, status);

        // Find associated transaction by transfer ID
        List<Transaction> transactions = transactionRepository.findByTransactionHash(transferId);

        if (transactions.isEmpty()) {
            log.warn("No transaction found for transfer: {}", transferId);
            return;
        }

        Transaction transaction = transactions.get(0);

        switch (status.toLowerCase()) {
            case "complete":
                log.info("Transfer completed for transaction: {}", transaction.getId());
                transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
                break;

            case "pending":
                log.info("Transfer pending for transaction: {}", transaction.getId());
                transaction.setStatus(Transaction.TransactionStatus.PROCESSING);
                break;

            case "failed":
                log.warn("Transfer failed for transaction: {}", transaction.getId());
                transaction.setStatus(Transaction.TransactionStatus.FAILED);
                if (data.getErrorCode() != null) {
                    transaction.setDescription("Transfer failed: " + data.getErrorCode());
                }
                break;
        }

        // Update transaction hash if available
        if (data.getTransactionHash() != null) {
            transaction.setTransactionHash(data.getTransactionHash());
        }

        transactionRepository.save(transaction);
        log.info("Transaction updated: id={}, status={}, txHash={}",
                transaction.getId(), transaction.getStatus(), data.getTransactionHash());
    }

    /**
     * Handle wallet-related webhook events
     */
    private void handleWalletEvent(CircleWebhookPayload payload) {
        CircleWebhookPayload.WebhookData data = payload.getData();
        String walletId = data.getId();

        log.info("Processing wallet event: walletId={}, status={}", walletId, data.getStatus());
        // TODO: Handle wallet creation or other wallet events
        // This might involve updating wallet metadata or syncing wallet state
    }

    /**
     * Retry failed webhooks
     */
    @Transactional
    public void retryFailedWebhooks() {
        log.info("Starting webhook retry process");

        List<Webhook> failedWebhooks = webhookRepository
                .findByStatusAndRetryCountLessThan(Webhook.WebhookStatus.RETRYING, MAX_RETRIES);

        for (Webhook webhook : failedWebhooks) {
            try {
                log.info("Retrying webhook: id={}, eventId={}, retryCount={}",
                        webhook.getId(), webhook.getEventId(), webhook.getRetryCount());

                webhook.setRetryCount(webhook.getRetryCount() + 1);
                webhook.setStatus(Webhook.WebhookStatus.PROCESSING);
                webhookRepository.save(webhook);

                // Parse and reprocess payload
                // TODO: Deserialize webhook.getPayload() and reprocess

                webhook.setStatus(Webhook.WebhookStatus.SUCCESS);
                webhookRepository.save(webhook);

                log.info("Webhook retry successful: {}", webhook.getEventId());

            } catch (Exception e) {
                log.error("Error retrying webhook: {}", webhook.getEventId(), e);
                webhook.setStatus(Webhook.WebhookStatus.FAILED);
                webhook.setErrorMessage(e.getMessage());
                webhookRepository.save(webhook);
            }
        }

        log.info("Webhook retry process completed");
    }

    /**
     * Get webhook by ID
     */
    public Webhook getWebhook(Long id) {
        return webhookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Webhook not found"));
    }

    /**
     * Get all failed webhooks
     */
    public List<Webhook> getFailedWebhooks() {
        return webhookRepository.findByStatus(Webhook.WebhookStatus.FAILED);
    }
}
