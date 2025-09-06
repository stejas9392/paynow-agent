DELETE FROM idempotency;
DELETE FROM balances;
DELETE FROM risk_signals;
DELETE FROM cases;

-- ✅ Balances table test data
INSERT OR IGNORE INTO balances (customer_id, balance_cents) VALUES
  (1001, 50000),   -- rich customer
  (1002, 1000),    -- low balance
  (1003, 0),       -- no balance
  (1004, 25000);   -- medium balance

-- ✅ Risk signals table test data
INSERT OR IGNORE INTO risk_signals (customer_id, recent_disputes, device_change) VALUES
  (1001, 0, 0),  -- safe profile
  (1002, 1, 0),  -- small risk
  (1003, 5, 3),  -- very risky (fraud-like)
  (1004, 0, 2);  -- device change risk

