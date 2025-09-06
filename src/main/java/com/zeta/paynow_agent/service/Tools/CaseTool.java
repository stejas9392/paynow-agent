package com.zeta.paynow_agent.service.Tools;

import com.zeta.paynow_agent.service.CaseService;
import org.springframework.stereotype.Service;

@Service
public class CaseTool implements ToolService<Boolean>{
    private final CaseService caseService;

    public CaseTool(CaseService caseService) {
        this.caseService = caseService;
    }

    @Override
    public String name() {
        return "createCase";
    }

    @Override
    public Boolean execute(Long customerId) {
        caseService.createcase(customerId,"manual_review","Triggered by agent decision");
        return  true;
    }
    @Override
    public String describe(Boolean result) {
        return result ? "Case created successfully" : "Case creation failed";
    }
}
