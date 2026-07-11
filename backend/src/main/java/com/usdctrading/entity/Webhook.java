package com.usdctrading.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "webhooks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Webhook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String resourceId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WebhookStatus status;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(nullable = false, updatable = false)
    private LocalDateTime receivedAt;

    @Column(nullable = false)
    private LocalDateTime processedAt;

    @Column(nullable = false)
    private Integer retryCount;

    @PrePersist
    protected void onCreate() {
        receivedAt = LocalDateTime.now();
        processedAt = LocalDateTime.now();
        retryCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        processedAt = LocalDateTime.now();
    }

    public enum WebhookStatus {
        PENDING,
        PROCESSING,
        SUCCESS,
        FAILED,
        RETRYING
    }
}
