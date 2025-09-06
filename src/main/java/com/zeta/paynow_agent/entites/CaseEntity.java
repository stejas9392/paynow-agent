package com.zeta.paynow_agent.entites;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="cases")
@Data
public class CaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private String decision; // REVIEW | BLOCK

    @Column(name = "reason_summary", nullable = false)
    private String reasonSummary;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
