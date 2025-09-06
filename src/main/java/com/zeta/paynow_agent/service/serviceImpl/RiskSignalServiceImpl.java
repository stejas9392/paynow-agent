package com.zeta.paynow_agent.service.serviceImpl;

import com.zeta.paynow_agent.model.RiskSignals;
import com.zeta.paynow_agent.repository.RiskSignalRepository;
import com.zeta.paynow_agent.service.RiskSignalsService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class RiskSignalServiceImpl implements RiskSignalsService {

    private final RiskSignalRepository riskSignalRepository;

    public RiskSignalServiceImpl(RiskSignalRepository riskSignalRepository) {
        this.riskSignalRepository = riskSignalRepository;
    }

    @Transactional
    @Override
    public RiskSignals getSignals(Long customerId) {
        return riskSignalRepository.findByCustomerId(customerId).map(customer->
                new RiskSignals(customer.getRecentDisputes(),customer.getDeviceChange()))
                .orElse(new RiskSignals(0,false));
    }
}
