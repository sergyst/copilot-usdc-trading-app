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
public class CirclePaymentResponse {
    @JsonProperty("data")
    private PaymentData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentData {
        @JsonProperty("id")
        private String id;

        @JsonProperty("status")
        private String status; // pending, confirmed, failed

        @JsonProperty("amount")
        private Amount amount;

        @JsonProperty("source")
        private Source source;

        @JsonProperty("description")
        private String description;

        @JsonProperty("createDate")
        private String createDate;

        @JsonProperty("updateDate")
        private String updateDate;

        @JsonProperty("errorCode")
        private String errorCode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Amount {
        @JsonProperty("amount")
        private String amount;

        @JsonProperty("currency")
        private String currency;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Source {
        @JsonProperty("type")
        private String type;

        @JsonProperty("id")
        private String id;
    }
}
