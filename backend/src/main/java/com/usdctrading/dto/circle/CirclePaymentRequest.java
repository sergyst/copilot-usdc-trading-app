package com.usdctrading.dto.circle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CirclePaymentRequest {
    @JsonProperty("idempotencyKey")
    private String idempotencyKey;

    @JsonProperty("amount")
    private Amount amount;

    @JsonProperty("source")
    private Source source;

    @JsonProperty("description")
    private String description;

    @JsonProperty("encryptedData")
    private String encryptedData; // For credit card payments

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Amount {
        @JsonProperty("amount")
        private String amount;

        @JsonProperty("currency")
        private String currency; // USD
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Source {
        @JsonProperty("type")
        private String type; // card or ach

        @JsonProperty("id")
        private String id; // Card or ACH ID
    }
}
