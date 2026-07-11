package com.usdctrading.dto;

import com.usdctrading.entity.Transaction;
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
public class TransactionResponse {
    private Long id;
    private String type;
    private BigDecimal amount;
    private BigDecimal pricePerUnit;
    private BigDecimal totalValue;
    private BigDecimal feeAmount;
    private String status;
    private String transactionHash;
    private String fromAddress;
    private String toAddress;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TransactionResponse fromEntity(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType().toString())
                .amount(transaction.getAmount())
                .pricePerUnit(transaction.getPricePerUnit())
                .totalValue(transaction.getTotalValue())
                .feeAmount(transaction.getFeeAmount())
                .status(transaction.getStatus().toString())
                .transactionHash(transaction.getTransactionHash())
                .fromAddress(transaction.getFromAddress())
                .toAddress(transaction.getToAddress())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
