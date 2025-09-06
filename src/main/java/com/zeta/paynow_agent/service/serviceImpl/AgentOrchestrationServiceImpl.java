package com.zeta.paynow_agent.service.serviceImpl;

import com.zeta.paynow_agent.model.*;
import com.zeta.paynow_agent.service.AgentOrchestrationService;
import com.zeta.paynow_agent.service.Tools.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AgentOrchestrationServiceImpl implements AgentOrchestrationService {

    private final BalanceTool balanceTool;
    private final CaseTool caseTool;
    private final RiskSignalTool riskSignalTool;
    private final ReserveBalanceTool reserveBalanceTool;

    public AgentOrchestrationServiceImpl(BalanceTool balanceTool, CaseTool caseTool, RiskSignalTool riskSignalTool, ReserveBalanceTool reserveBalanceTool) {
        this.balanceTool = balanceTool;
        this.caseTool = caseTool;
        this.riskSignalTool = riskSignalTool;
        this.reserveBalanceTool = reserveBalanceTool;
    }


    @Override
    public Result decide(Long customerId, BigDecimal amount) {
        List<AgentTraceStep> agentTraceStepList = new ArrayList<>();
        List<String> reasons=new ArrayList<>();

        agentTraceStepList.add(new AgentTraceStep("plan","Check balance," +
                "risk, and limits"));

        BigDecimal balance= (BigDecimal) callWithRetry(balanceTool,customerId,agentTraceStepList);
        if(balance == null){
            reasons.add("balance_unavailable");
            return new Result(Decision.REVIEW, agentTraceStepList, reasons);
        }

        RiskSignals riskSignals= (RiskSignals) callWithRetry(riskSignalTool,customerId,agentTraceStepList);
        if(riskSignals==null){
            reasons.add("risk_signals_unavailable");
            return new Result(Decision.REVIEW, agentTraceStepList, reasons);
        }

        Decision decision = Decision.ALLOW;
        if (balance.compareTo(amount) < 0) {
            log.info("insufficient_balance");
            decision = Decision.BLOCK;
            reasons.add("insufficient_balance");
        }

        // Rule 2: Risk signals
        if (riskSignals.recentDisputes() > 3) {
            log.info("risk_disputes");
            decision = Decision.REVIEW;
            reasons.add("recent_disputes");
        }
        if (riskSignals.deviceChange()) {
            log.info("device_change");
            decision = Decision.REVIEW;
            reasons.add("device_change");
        }

        // Default if no issues
        if (reasons.isEmpty()) {
            reasons.add("sufficient_balance_and_low_risk");
        }

        agentTraceStepList.add(new AgentTraceStep("tool:recommend", decision.name() + " — " + String.join(", ", reasons)));
        if (decision == Decision.ALLOW) {
            boolean reserved = reserveBalanceTool.execute(new ReserveBalanceRequest(customerId,amount));
            if (!reserved) {
                log.info("Race condition :balance not available");
                decision = Decision.BLOCK;
                reasons.clear();
                reasons.add("Race condition: balance not available");
                agentTraceStepList.add(new AgentTraceStep("tool:reserveBalance", decision.name() + " — " + String.join(", ", reasons)));
            }
        } else {
            caseTool.execute(customerId);
            agentTraceStepList.add(new AgentTraceStep("tool:createCase", decision.name() + " — " + String.join(", ", reasons)));
        }

        return new Result(decision, agentTraceStepList, reasons);
    }
    private <T> T callWithRetry(ToolService <T> tool,Long customerId,List<AgentTraceStep> trace){
        for(int attempt=1;attempt<=2;attempt++){
            log.info("attempt {} for tool call ",attempt);
            try {
                T result=tool.execute(customerId);
                trace.add(new AgentTraceStep("tool:"+tool.name(), tool.describe(result)));
                return result;
            }
            catch (Exception e) {
                trace.add(new AgentTraceStep("tool:" + tool.name(),
                        "error (attempt " + attempt + "): " + e.getMessage()));
            }
        }
        return null;
    }

}
