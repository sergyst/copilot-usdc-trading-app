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
public class CircleWalletRequest {
    @JsonProperty("idempotencyKey")
    private String idempotencyKey;

    @JsonProperty("description")
    private String description;
}
