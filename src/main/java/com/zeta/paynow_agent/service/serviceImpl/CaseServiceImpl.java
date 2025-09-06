package com.zeta.paynow_agent.service.serviceImpl;

import com.zeta.paynow_agent.entites.CaseEntity;
import com.zeta.paynow_agent.repository.CaseRepository;
import com.zeta.paynow_agent.service.CaseService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@Slf4j
public class CaseServiceImpl implements CaseService {

    private final CaseRepository caseRepository;

    public CaseServiceImpl(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    @Transactional
    @Override
    public void createcase(Long customerId, String decision, String reasonSummary) {
        CaseEntity newCase= new CaseEntity();
        newCase.setDecision(decision);
        newCase.setCustomerId(customerId);
        newCase.setReasonSummary(reasonSummary);
        caseRepository.save(newCase);
        log.info("case created successfully");

    }
}
