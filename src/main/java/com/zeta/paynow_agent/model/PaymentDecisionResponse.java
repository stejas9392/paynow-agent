package com.zeta.paynow_agent.model;

import java.util.List;

public record PaymentDecisionResponse(Decision decision, List<String> reasons, List<AgentTraceStep> agentTrace, String requestId) {
}
