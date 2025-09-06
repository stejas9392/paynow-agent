package com.zeta.paynow_agent.entites;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="idempotency",uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "idempotency_key"})
)
@Data
public class IdempotencyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="customer_id",nullable = false)
    private Long customerId;

    @Column(name="idempotency_key",nullable = false)
    private String idempotencyKey;

    @Column(nullable = false)
    private String status;

    @Lob
    private String responseJson;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
