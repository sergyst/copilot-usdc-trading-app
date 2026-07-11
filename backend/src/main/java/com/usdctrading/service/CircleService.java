package com.usdctrading.service;

import com.usdctrading.dto.circle.*;
import com.usdctrading.exception.CircleApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
public class CircleService {

    @Value("${circle.api-key}")
    private String apiKey;

    @Value("${circle.api-url}")
    private String apiUrl;

    @Value("${circle.version:v1}")
    private String apiVersion;

    private final WebClient webClient;

    public CircleService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Create a new wallet via Circle API
     * POST /v1/wallets
     */
    public CircleWalletResponse.WalletData createWallet(String description) {
        log.info("Creating wallet with description: {}", description);

        CircleWalletRequest request = CircleWalletRequest.builder()
                .idempotencyKey(UUID.randomUUID().toString())
                .description(description)
                .build();

        try {
            CircleWalletResponse response = webClient
                    .post()
                    .uri(apiUrl + "/" + apiVersion + "/wallets")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(CircleErrorResponse.class)
                                    .flatMap(errorResponse -> Mono.error(
                                            new CircleApiException(
                                                    "Circle API error: " + errorResponse.getMessage(),
                                                    errorResponse.getCode(),
                                                    errorResponse.getMessage()
                                            )
                                    ))
                    )
                    .bodyToMono(CircleWalletResponse.class)
                    .block();

            log.info("Wallet created successfully with ID: {}", response.getData().getId());
            return response.getData();
        } catch (WebClientResponseException e) {
            log.error("Error creating wallet: Status={}, Message={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CircleApiException("Failed to create wallet: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error creating wallet", e);
            throw new CircleApiException("Failed to create wallet", e);
        }
    }

    /**
     * Get wallet balance via Circle API
     * GET /v1/wallets/{walletId}
     */
    public CircleBalanceResponse.BalanceData getWalletBalance(String walletId) {
        log.info("Fetching balance for wallet: {}", walletId);

        try {
            CircleBalanceResponse response = webClient
                    .get()
                    .uri(apiUrl + "/" + apiVersion + "/wallets/" + walletId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(CircleErrorResponse.class)
                                    .flatMap(errorResponse -> Mono.error(
                                            new CircleApiException(
                                                    "Circle API error: " + errorResponse.getMessage(),
                                                    errorResponse.getCode(),
                                                    errorResponse.getMessage()
                                            )
                                    ))
                    )
                    .bodyToMono(CircleBalanceResponse.class)
                    .block();

            log.info("Balance retrieved for wallet: {}", walletId);
            return response.getData();
        } catch (WebClientResponseException e) {
            log.error("Error fetching balance: Status={}, Message={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CircleApiException("Failed to fetch balance: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error fetching balance", e);
            throw new CircleApiException("Failed to fetch balance", e);
        }
    }

    /**
     * Transfer USDC between wallets or to blockchain address
     * POST /v1/transfers
     */
    public CircleTransferResponse.TransferData transferUsdc(
            String sourceId,
            String destinationId,
            BigDecimal amount,
            String sourceType,
            String destinationType) {
        log.info("Transferring {} USDC from {} to {}", amount, sourceId, destinationId);

        CircleTransferRequest request = CircleTransferRequest.builder()
                .idempotencyKey(UUID.randomUUID().toString())
                .source(CircleTransferRequest.Source.builder()
                        .type(sourceType)
                        .id(sourceId)
                        .build())
                .destination(CircleTransferRequest.Destination.builder()
                        .type(destinationType)
                        .id(destinationId)
                        .build())
                .amount(CircleTransferRequest.Amount.builder()
                        .amount(amount)
                        .currency("USD") // USDC is treated as USD
                        .build())
                .build();

        try {
            CircleTransferResponse response = webClient
                    .post()
                    .uri(apiUrl + "/" + apiVersion + "/transfers")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(CircleErrorResponse.class)
                                    .flatMap(errorResponse -> Mono.error(
                                            new CircleApiException(
                                                    "Circle API error: " + errorResponse.getMessage(),
                                                    errorResponse.getCode(),
                                                    errorResponse.getMessage()
                                            )
                                    ))
                    )
                    .bodyToMono(CircleTransferResponse.class)
                    .block();

            log.info("Transfer completed with ID: {}", response.getData().getId());
            return response.getData();
        } catch (WebClientResponseException e) {
            log.error("Error transferring USDC: Status={}, Message={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CircleApiException("Failed to transfer USDC: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error transferring USDC", e);
            throw new CircleApiException("Failed to transfer USDC", e);
        }
    }

    /**
     * Process payment to buy USDC
     * POST /v1/payments
     */
    public CirclePaymentResponse.PaymentData processPayment(
            String amount,
            String sourceId,
            String sourceType,
            String description) {
        log.info("Processing payment of {} USD via {}", amount, sourceType);

        CirclePaymentRequest request = CirclePaymentRequest.builder()
                .idempotencyKey(UUID.randomUUID().toString())
                .amount(CirclePaymentRequest.Amount.builder()
                        .amount(amount)
                        .currency("USD")
                        .build())
                .source(CirclePaymentRequest.Source.builder()
                        .type(sourceType)
                        .id(sourceId)
                        .build())
                .description(description)
                .build();

        try {
            CirclePaymentResponse response = webClient
                    .post()
                    .uri(apiUrl + "/" + apiVersion + "/payments")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(CircleErrorResponse.class)
                                    .flatMap(errorResponse -> Mono.error(
                                            new CircleApiException(
                                                    "Circle API error: " + errorResponse.getMessage(),
                                                    errorResponse.getCode(),
                                                    errorResponse.getMessage()
                                            )
                                    ))
                    )
                    .bodyToMono(CirclePaymentResponse.class)
                    .block();

            log.info("Payment processed with ID: {}", response.getData().getId());
            return response.getData();
        } catch (WebClientResponseException e) {
            log.error("Error processing payment: Status={}, Message={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CircleApiException("Failed to process payment: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error processing payment", e);
            throw new CircleApiException("Failed to process payment", e);
        }
    }

    /**
     * Get transfer status
     * GET /v1/transfers/{transferId}
     */
    public CircleTransferResponse.TransferData getTransferStatus(String transferId) {
        log.info("Fetching transfer status: {}", transferId);

        try {
            CircleTransferResponse response = webClient
                    .get()
                    .uri(apiUrl + "/" + apiVersion + "/transfers/" + transferId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(CircleErrorResponse.class)
                                    .flatMap(errorResponse -> Mono.error(
                                            new CircleApiException(
                                                    "Circle API error: " + errorResponse.getMessage(),
                                                    errorResponse.getCode(),
                                                    errorResponse.getMessage()
                                            )
                                    ))
                    )
                    .bodyToMono(CircleTransferResponse.class)
                    .block();

            log.info("Transfer status: {}", response.getData().getStatus());
            return response.getData();
        } catch (WebClientResponseException e) {
            log.error("Error fetching transfer status: Status={}, Message={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CircleApiException("Failed to fetch transfer status: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error fetching transfer status", e);
            throw new CircleApiException("Failed to fetch transfer status", e);
        }
    }

    /**
     * Get payment status
     * GET /v1/payments/{paymentId}
     */
    public CirclePaymentResponse.PaymentData getPaymentStatus(String paymentId) {
        log.info("Fetching payment status: {}", paymentId);

        try {
            CirclePaymentResponse response = webClient
                    .get()
                    .uri(apiUrl + "/" + apiVersion + "/payments/" + paymentId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(CircleErrorResponse.class)
                                    .flatMap(errorResponse -> Mono.error(
                                            new CircleApiException(
                                                    "Circle API error: " + errorResponse.getMessage(),
                                                    errorResponse.getCode(),
                                                    errorResponse.getMessage()
                                            )
                                    ))
                    )
                    .bodyToMono(CirclePaymentResponse.class)
                    .block();

            log.info("Payment status: {}", response.getData().getStatus());
            return response.getData();
        } catch (WebClientResponseException e) {
            log.error("Error fetching payment status: Status={}, Message={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CircleApiException("Failed to fetch payment status: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error fetching payment status", e);
            throw new CircleApiException("Failed to fetch payment status", e);
        }
    }
}
