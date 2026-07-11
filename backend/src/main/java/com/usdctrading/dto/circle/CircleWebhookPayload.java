package com.usdctrading.dto.circle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleWebhookPayload {
    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("version")
    private Integer version;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("data")
    private WebhookData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WebhookData {
        @JsonProperty("id")
        private String id;

        @JsonProperty("type")
        private String type;

        @JsonProperty("status")
        private String status;

        @JsonProperty("amount")
        private Amount amount;

        @JsonProperty("source")
        private Source source;

        @JsonProperty("destination")
        private Destination destination;

        @JsonProperty("transactionHash")
        private String transactionHash;

        @JsonProperty("errorCode")
        private String errorCode;

        @JsonProperty("description")
        private String description;

        @JsonProperty("createDate")
        private String createDate;

        @JsonProperty("updateDate")
        private String updateDate;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Destination {
        @JsonProperty("type")
        private String type;

        @JsonProperty("id")
        private String id;
    }
}
