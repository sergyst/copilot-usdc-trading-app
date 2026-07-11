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
public class CircleTransferResponse {
    @JsonProperty("data")
    private TransferData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferData {
        @JsonProperty("id")
        private String id;

        @JsonProperty("source")
        private Source source;

        @JsonProperty("destination")
        private Destination destination;

        @JsonProperty("amount")
        private Amount amount;

        @JsonProperty("transactionHash")
        private String transactionHash;

        @JsonProperty("status")
        private String status;

        @JsonProperty("createDate")
        private String createDate;

        @JsonProperty("updateDate")
        private String updateDate;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Amount {
        @JsonProperty("amount")
        private BigDecimal amount;

        @JsonProperty("currency")
        private String currency;
    }
}
