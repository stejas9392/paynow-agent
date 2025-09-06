package com.zeta.paynow_agent.model;

import lombok.Data;


public record RiskSignals(int recentDisputes, boolean deviceChange) {}