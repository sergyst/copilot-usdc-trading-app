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
public class CircleWalletResponse {
    @JsonProperty("data")
    private WalletData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalletData {
        @JsonProperty("id")
        private String id;

        @JsonProperty("status")
        private String status;

        @JsonProperty("description")
        private String description;

        @JsonProperty("createDate")
        private String createDate;
    }
}
