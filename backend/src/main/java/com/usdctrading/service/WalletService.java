package com.usdctrading.service;

import com.usdctrading.dto.WalletResponse;
import com.usdctrading.entity.User;
import com.usdctrading.entity.Wallet;
import com.usdctrading.repository.UserRepository;
import com.usdctrading.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EthereumService ethereumService;

    /**
     * Get all wallets for a user
     */
    public List<WalletResponse> getUserWallets(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return walletRepository.findByUser(user).stream()
                .map(WalletResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get wallet details
     */
    public WalletResponse getWallet(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        return WalletResponse.fromEntity(wallet);
    }

    /**
     * Create a new wallet for a user
     */
    public WalletResponse createWallet(Long userId, String walletAddress, Wallet.WalletType walletType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if wallet already exists
        if (walletRepository.findByWalletAddress(walletAddress).isPresent()) {
            throw new RuntimeException("Wallet already exists");
        }

        Wallet wallet = Wallet.builder()
                .user(user)
                .walletAddress(walletAddress)
                .walletType(walletType)
                .isDefault(false)
                .build();

        Wallet savedWallet = walletRepository.save(wallet);
        log.info("Wallet created for user: {} with address: {}", userId, walletAddress);

        return WalletResponse.fromEntity(savedWallet);
    }

    /**
     * Update wallet balance from blockchain
     */
    public WalletResponse refreshWalletBalance(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // Fetch balance from blockchain
        try {
            String usdcBalance = ethereumService.getUsdcBalance(wallet.getWalletAddress());
            // Update wallet balance (convert to BigDecimal)
            // wallet.setUsdcBalance(new BigDecimal(usdcBalance));
            walletRepository.save(wallet);
            log.info("Wallet balance refreshed for: {}", wallet.getWalletAddress());
        } catch (Exception e) {
            log.error("Error refreshing wallet balance", e);
            throw new RuntimeException("Failed to refresh balance: " + e.getMessage());
        }

        return WalletResponse.fromEntity(wallet);
    }

    /**
     * Set default wallet for a user
     */
    public WalletResponse setDefaultWallet(Long userId, Long walletId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (!wallet.getUser().getId().equals(userId)) {
            throw new RuntimeException("Wallet does not belong to user");
        }

        // Reset other default wallets
        walletRepository.findByUserAndIsDefault(user, true).ifPresent(w -> {
            w.setIsDefault(false);
            walletRepository.save(w);
        });

        // Set this wallet as default
        wallet.setIsDefault(true);
        Wallet updatedWallet = walletRepository.save(wallet);
        log.info("Default wallet set for user: {}", userId);

        return WalletResponse.fromEntity(updatedWallet);
    }
}
