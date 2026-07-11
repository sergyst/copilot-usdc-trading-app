package com.usdctrading.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebhookSignatureValidator {

    /**
     * Validate Circle webhook signature
     * Circle signs each webhook with HMAC-SHA256
     *
     * @param payload The raw webhook payload
     * @param signature The signature from Circle header
     * @param secret Your webhook secret from Circle dashboard
     * @return true if signature is valid
     */
    public static boolean validateSignature(String payload, String signature, String secret) {
        try {
            // Create HMAC-SHA256 signature
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(secretKey);

            byte[] messageBytes = payload.getBytes();
            byte[] hashBytes = mac.doFinal(messageBytes);

            // Encode as base64
            String calculatedSignature = Base64.getEncoder().encodeToString(hashBytes);

            // Compare with provided signature
            boolean isValid = calculatedSignature.equals(signature);
            if (!isValid) {
                log.warn("Webhook signature validation failed. Expected: {}, Got: {}",
                        calculatedSignature, signature);
            }
            return isValid;
        } catch (Exception e) {
            log.error("Error validating webhook signature", e);
            return false;
        }
    }
}
