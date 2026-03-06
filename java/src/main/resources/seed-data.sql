-- Seed data: 10 deposits across all plan types covering all day boundaries
-- Used by TestTimeDepositApplication with seed profile

INSERT INTO time_deposits (id, plan_type, days, balance) VALUES
    (1, 'basic', 15, 1000.00),     -- no interest (≤30 days)
    (2, 'basic', 30, 2000.00),     -- no interest (=30 days, boundary)
    (3, 'basic', 31, 3000.00),     -- earns interest (>30 days)
    (4, 'basic', 365, 5000.00),    -- earns interest
    (5, 'student', 30, 1000.00),   -- no interest (≤30 days)
    (6, 'student', 31, 2000.00),   -- earns interest (>30, ≤365)
    (7, 'student', 365, 4000.00),  -- earns interest (=365, boundary)
    (8, 'student', 366, 3000.00),  -- no interest (>365 days)
    (9, 'premium', 45, 10000.00),  -- no interest (≤45 days)
    (10, 'premium', 46, 20000.00); -- earns interest (>45 days)

-- 3 withdrawals linked to deposits
INSERT INTO withdrawals (id, time_deposit_id, amount, date) VALUES
    (1, 3, 500.00, '2024-01-15'),
    (2, 6, 200.00, '2024-02-20'),
    (3, 10, 1000.00, '2024-03-10');

-- Reset sequences past inserted IDs
SELECT setval('time_deposits_seq', 100);
SELECT setval('withdrawals_seq', 100);
