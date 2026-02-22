package com.spring.tradexportfolioservice.Models;

import com.spring.tradexportfolioservice.Enums.IdempotencyStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "idempotency_keys")
public class IdempotencyKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_value", unique = true, nullable = false, updatable = false)
    private String keyValue;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private IdempotencyStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static IdempotencyKey create(String keyValue, Long userId) {
        IdempotencyKey key = new IdempotencyKey();
        key.keyValue = keyValue;
        key.userId = userId;
        key.status = IdempotencyStatus.PENDING;
        return key;
    }

    public void markCompleted() {
        if (this.status != IdempotencyStatus.PENDING) {
            throw new IllegalStateException("Cannot complete a key that is not PENDING");
        }
        this.status = IdempotencyStatus.COMPLETED;
    }

    public void markFailed() {
        if (this.status != IdempotencyStatus.PENDING) {
            throw new IllegalStateException("Cannot fail a key that is not PENDING");
        }
        this.status = IdempotencyStatus.FAILED;
    }
}
