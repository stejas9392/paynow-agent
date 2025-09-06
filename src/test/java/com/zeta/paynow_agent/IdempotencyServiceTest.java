package com.zeta.paynow_agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.paynow_agent.entites.IdempotencyEntity;
import com.zeta.paynow_agent.repository.IdempotencyRepository;
import com.zeta.paynow_agent.service.IdempotencyService;
import com.zeta.paynow_agent.service.serviceImpl.IdempotencyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:sqlite::memory:",
        "spring.datasource.driver-class-name=org.sqlite.JDBC"
})
class IdempotencyServiceTest {

    @Autowired
    private IdempotencyRepository idempotencyRepository;

    private IdempotencyService idempotencyService;

    @BeforeEach
    void setup() {
        idempotencyService = new IdempotencyServiceImpl(idempotencyRepository, new ObjectMapper());

        // Seed data into the in-memory database
        IdempotencyEntity entity = new IdempotencyEntity();
        entity.setCustomerId(1009L);
        entity.setIdempotencyKey("uuid-9");
        entity.setStatus("COMPLETED");
        entity.setResponseJson("{\"decision\":\"ALLOW\"}");

        idempotencyRepository.save(entity);
    }

    @Test
    void returnsCompletedResponseIfExists() {
        Optional<String> result = idempotencyService.getCompletedResponse(1009L, "uuid-9");

        assertThat(result).isPresent();
        assertThat(result.get()).contains("ALLOW");
    }
}
