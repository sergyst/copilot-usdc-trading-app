package com.usdctrading.controller;

import com.usdctrading.dto.WalletResponse;
import com.usdctrading.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/wallets")
@CrossOrigin(origins = "*")
public class WalletController {

    @Autowired
    private WalletService walletService;

    /**
     * Get all wallets for a user
     * GET /wallets
     */
    @GetMapping
    public ResponseEntity<List<WalletResponse>> getUserWallets(
            @RequestHeader("Authorization") String authorization) {
        log.info("Fetching wallets for user");
        Long userId = extractUserIdFromToken(authorization);
        
        List<WalletResponse> wallets = walletService.getUserWallets(userId);
        return ResponseEntity.ok(wallets);
    }

    /**
     * Get wallet details
     * GET /wallets/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<WalletResponse> getWallet(
            @PathVariable @NotNull Long id) {
        log.info("Fetching wallet: {}", id);
        
        WalletResponse wallet = walletService.getWallet(id);
        return ResponseEntity.ok(wallet);
    }

    /**
     * Get USDC balance for a wallet
     * GET /wallets/{id}/balance
     */
    @GetMapping("/{id}/balance")
    public ResponseEntity<WalletResponse> getWalletBalance(
            @PathVariable @NotNull Long id) {
        log.info("Fetching balance for wallet: {}", id);
        
        WalletResponse wallet = walletService.getWallet(id);
        return ResponseEntity.ok(wallet);
    }

    /**
     * Extract user ID from JWT token
     */
    private Long extractUserIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // TODO: Parse JWT token and extract userId
            return 1L;
        }
        throw new RuntimeException("Invalid authorization header");
    }
}
