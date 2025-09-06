package com.zeta.paynow_agent.entites;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name="balances")
@Data
public class BalanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="customer_id",nullable = false)
    private Long customerId;
    @Column(name="balance_cents",nullable = false)
    private BigDecimal balanceCents;


}
