package com.zeta.paynow_agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.paynow_agent.entites.BalanceEntity;
import com.zeta.paynow_agent.entites.RiskSignalEntity;
import com.zeta.paynow_agent.repository.BalanceRepository;
import com.zeta.paynow_agent.repository.RiskSignalRepository;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class PaymentControllerTest {

    @Autowired
    private   MockMvc mockMvc;

    @Autowired
    private   BalanceRepository balanceRepository;

    @Autowired
    private   RiskSignalRepository riskSignalsRepository ;

    @Autowired
    private   ObjectMapper  objectMapper;

    private static final Long TEST_CUSTOMER_ID = 1011L;




    @BeforeAll
        void setup() {
        // Seed balance
        BalanceEntity balance = new BalanceEntity();
        balance.setCustomerId(TEST_CUSTOMER_ID);
        balance.setBalanceCents(BigDecimal.valueOf(50000.0));  // $500
        balanceRepository.save(balance);

        // Seed risk signals
        RiskSignalEntity risk = new RiskSignalEntity();
        risk.setCustomerId(TEST_CUSTOMER_ID);
        risk.setRecentDisputes(0);
        risk.setDeviceChange(false);
        riskSignalsRepository.save(risk);
    }
//    @AfterAll
//    @Transactional
//     void cleanupAll() {
//        // This method runs once after all tests in this class
//        System.out.println("Executing @AfterAll: Cleaning up test data.");
//
//        // Clean up the data created in setupAll
//        balanceRepository.deleteByCustomerId(TEST_CUSTOMER_ID);
//        riskSignalsRepository.deleteByCustomerId(TEST_CUSTOMER_ID);
//    }

    @Test
    void firstPostShouldAllowDecision() throws Exception {
        var payload = Map.of(
         "customerId", TEST_CUSTOMER_ID, "amount", 500, "currency", "USD", "payeeId", "p_789", "idempotencyKey", "uuid-4"

        );

        mockMvc.perform(post("/payments/decide")
                        .header("X-API-Key", "dev-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value("ALLOW"));
    }

    @Test
    void repeatedPostShouldReturnSameDecision() throws Exception {
        var payload = Map.of(
                "customerId", TEST_CUSTOMER_ID, "amount", 540, "currency", "USD", "payeeId", "p_789", "idempotencyKey", "uuid-3"
        );

        // First call
        mockMvc.perform(post("/payments/decide")
                        .header("X-API-Key", "dev-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value("ALLOW"));

        // Second call (idempotent response)
        mockMvc.perform(post("/payments/decide")
                        .header("X-API-Key", "dev-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value("ALLOW"));  // Same result, no duplicate processing
    }

}
