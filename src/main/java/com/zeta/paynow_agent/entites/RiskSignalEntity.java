package com.zeta.paynow_agent.entites;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="risk_signals")
@Data
public class RiskSignalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "recent_disputes", nullable = false)
    private int recentDisputes;

    @Column(name = "device_change", nullable = false)
    private Boolean deviceChange;
}
