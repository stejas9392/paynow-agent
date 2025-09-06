package com.zeta.paynow_agent.service;

import java.util.UUID;

public interface CaseService {
    void createcase(Long customerId, String decision, String reasonSummary);
}
