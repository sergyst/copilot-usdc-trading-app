package com.usdctrading.dto;

import com.usdctrading.entity.Wallet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletResponse {
    private Long id;
    private String walletAddress;
    private BigDecimal usdcBalance;
    private BigDecimal ethBalance;
    private String walletType;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WalletResponse fromEntity(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .walletAddress(wallet.getWalletAddress())
                .usdcBalance(wallet.getUsdcBalance())
                .ethBalance(wallet.getEthBalance())
                .walletType(wallet.getWalletType().toString())
                .isDefault(wallet.getIsDefault())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }
}
