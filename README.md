# paynow-agent
# Zeta PayNow Agent - Backend Service

This project implements a  backend service for processing payment decisions, following clean code principles, SOLID architecture, and meeting all requirements from the assessment document.

---

## 🚀 How to Run Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/stejas9392/paynow-agent.git

2. Build the project:
   ./mvnw clean install

3. Run the project:
   ./mvnw spring-boot:run

4. Required Java Version - 21

🧱 Architecture Diagram

+--------------------------------------------------------------+
|                        HTTP Client (Postman, FE)           |
+--------------------------+-----------------------------------+
|
v
+--------------------------+-----------------------------------+
|               PaymentController (REST API)                   |
|         - Handles /payments/decide POST requests             |
|             - Validates API key, RequestId                   |
+--------------------------+-----------------------------------+
|
v
+--------------------------+-----------------------------------+
|                 PaymentService (Business Logic)              |
|          - Rate Limiting (RateLimiterService)                |
|       - Idempotency Handling (IdempotencyService)            |
|      - Agent Orchestration (AgentOrchestrationService)       |
|           * Calls BalanceTool, RiskSignalTool, etc.          |
|              - Metrics Recording (MetricsService)            |
+--------------------------+-----------------------------------+
|
v
+--------------------------+-----------------------------------+
|                        Repository Layer                    |
| - JPA Repositories (IdempotencyRepository,                 |
|   BalanceRepository, RiskSignalRepository, CaseRepository) |
+--------------------------+-----------------------------------+
|
v
+--------------------------+-----------------------------------+
|                         SQLite Database                   |
| - Tables: idempotency, balances, risk_signals, cases      |
| - schema.sql for DB schema                                |
+--------------------------+-----------------------------------+

Additional Features:
- Logging Filters → Correlates logs using requestId
- API Key Filter → Validates API key header
- Rate Limiter → Limits requests per customerId
- Idempotency → Ensures request deduplication
- Metrics → Exposed via /metrics endpoint
- Agent Trace → Keeps track of tool orchestration steps



✅ What We Optimized

✅ Simplicity: Clear separation of concerns between controller, service, and repository layers.

✅ Idempotency: Prevent repeated decisions for the same request.

✅ Rate limiting: Ensures max 5 requests/sec per customer.

✅ Observability: Logs with requestId correlation, metrics at /actuator/metrics.

✅ Security: API Key header + redact customerId from logs.


⚡ Trade-offs Made
Feature                |    Choice & Reason
-------------------------------------------------------------------------------------------------------
Rate Limiter	       |    In-memory token bucket → Easy to implement, suitable for single instance.
Data Store	           |    SQLite → Simple local setup, good for test/dev environment.
Agent orchestration	   |    Plain orchestration class → Lightweight, no external frameworks required.
Logging	               |    logback + SLF4J → Simple and production-ready logging mechanism.


✅ Sample cURL Command
curl -X POST http://localhost:8080/payments/decide \
-H "Content-Type: application/json" \
-H "X-API-Key: your_api_key_here" \
-d '{
"customerId": 1001,
"amount": 500,
"currency": "USD",
"payeeId": "p_789",
"idempotencyKey": "uuid-1"
}'


✅ Performance & Security Notes

Performance optimized by early validations, in-memory rate limiting, and efficient DB usage.

CustomerId is considered PII → Masked in logs using ***last3digits.

API key required to secure endpoints.
