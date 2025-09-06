package com.zeta.paynow_agent.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PaymentDecisionRequest(@NotNull Long customerId, @NotNull @PositiveOrZero BigDecimal amount,
                                     @NotBlank String currency,@NotBlank String payeeId,@NotBlank String idempotencyKey) {
}
