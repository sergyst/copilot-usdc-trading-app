package com.usdctrading.controller;

import com.usdctrading.dto.BuyUsdcRequest;
import com.usdctrading.dto.SellUsdcRequest;
import com.usdctrading.dto.TransactionResponse;
import com.usdctrading.dto.OrderResponse;
import com.usdctrading.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "*")
@Validated
public class TradeController {

    @Autowired
    private TradeService tradeService;

    /**
     * Buy USDC tokens
     * POST /transactions/buy
     * Request body: BuyUsdcRequest
     */
    @PostMapping("/buy")
    public ResponseEntity<OrderResponse> buyUsdc(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody BuyUsdcRequest request) {
        log.info("Buy USDC request received");
        
        // Extract userId from token (implement based on your JWT implementation)
        Long userId = extractUserIdFromToken(authorization);
        
        OrderResponse response = tradeService.buyUsdc(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Sell USDC tokens
     * POST /transactions/sell
     * Request body: SellUsdcRequest
     */
    @PostMapping("/sell")
    public ResponseEntity<OrderResponse> sellUsdc(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody SellUsdcRequest request) {
        log.info("Sell USDC request received");
        
        // Extract userId from token
        Long userId = extractUserIdFromToken(authorization);
        
        OrderResponse response = tradeService.sellUsdc(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all transactions for a user
     * GET /transactions
     */
    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching transactions for user");
        
        Long userId = extractUserIdFromToken(authorization);
        Pageable pageable = PageRequest.of(page, size);
        
        Page<TransactionResponse> transactions = tradeService.getUserTransactions(userId, pageable);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get transaction details
     * GET /transactions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable @NotNull Long id) {
        log.info("Fetching transaction: {}", id);
        
        TransactionResponse transaction = tradeService.getTransaction(id);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Get all orders for a user
     * GET /orders
     */
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching orders for user");
        
        Long userId = extractUserIdFromToken(authorization);
        Pageable pageable = PageRequest.of(page, size);
        
        Page<OrderResponse> orders = tradeService.getUserOrders(userId, pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get order details
     * GET /orders/{id}
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable @NotNull Long id) {
        log.info("Fetching order: {}", id);
        
        OrderResponse order = tradeService.getOrder(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Cancel an order
     * DELETE /orders/{id}
     */
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable @NotNull Long id,
            @RequestHeader("Authorization") String authorization) {
        log.info("Cancelling order: {}", id);
        
        Long userId = extractUserIdFromToken(authorization);
        OrderResponse response = tradeService.cancelOrder(id, userId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Extract user ID from JWT token
     * TODO: Implement proper JWT token extraction
     */
    private Long extractUserIdFromToken(String authHeader) {
        // Remove "Bearer " prefix if present
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // TODO: Parse JWT token and extract userId
            // For now, return a placeholder
            return 1L;
        }
        throw new RuntimeException("Invalid authorization header");
    }
}
