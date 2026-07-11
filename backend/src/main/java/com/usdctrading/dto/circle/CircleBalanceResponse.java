package com.usdctrading.dto.circle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleBalanceResponse {
    @JsonProperty("data")
    private BalanceData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BalanceData {
        @JsonProperty("walletId")
        private String walletId;

        @JsonProperty("tokenBalances")
        private List<TokenBalance> tokenBalances;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenBalance {
        @JsonProperty("token")
        private String token;

        @JsonProperty("amount")
        private BigDecimal amount;

        @JsonProperty("updateDate")
        private String updateDate;
    }
}
