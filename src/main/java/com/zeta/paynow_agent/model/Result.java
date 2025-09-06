package com.zeta.paynow_agent.model;

import java.util.List;

public record Result(Decision decision, List<AgentTraceStep> trace, List<String>reason) {}

