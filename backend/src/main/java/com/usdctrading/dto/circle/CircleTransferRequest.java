package com.usdctrading.dto.circle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleTransferRequest {
    @JsonProperty("idempotencyKey")
    private String idempotencyKey;

    @JsonProperty("source")
    private Source source;

    @JsonProperty("destination")
    private Destination destination;

    @JsonProperty("amount")
    private Amount amount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Source {
        @JsonProperty("type")
        private String type; // wallet or blockchain

        @JsonProperty("id")
        private String id; // wallet ID or address
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Destination {
        @JsonProperty("type")
        private String type; // wallet or blockchain

        @JsonProperty("id")
        private String id; // wallet ID or address
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Amount {
        @JsonProperty("amount")
        private BigDecimal amount;

        @JsonProperty("currency")
        private String currency; // USD or USDC
    }
}
