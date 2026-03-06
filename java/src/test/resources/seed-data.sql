-- Seed data: 10 deposits across all plan types covering all day boundaries
-- Used by TestTimeDepositApplication with seed profile

INSERT INTO time_deposits (id, plan_type, days, balance)
VALUES (1, 'basic', 29, 1000.00),    -- no interest (below 30-day threshold)
       (2, 'basic', 31, 1000.00),    -- earns interest (above 30 days)
       (3, 'basic', 365, 5000.00),   -- earns interest (no cap for basic)
       (4, 'student', 30, 2000.00),  -- no interest (at 30-day boundary)
       (5, 'student', 31, 2000.00),  -- earns interest (above 30 days)
       (6, 'student', 365, 2000.00), -- earns interest (at 365-day boundary)
       (7, 'student', 366, 2000.00), -- no interest (past 365-day cap)
       (8, 'premium', 44, 10000.00), -- no interest (below 45-day threshold)
       (9, 'premium', 45, 10000.00), -- no interest (at 45-day boundary)
       (10, 'premium', 46, 10000.00);
-- earns interest (above 45 days)

-- 3 withdrawals linked to deposits
INSERT INTO withdrawals (id, time_deposit_id, amount, date)
VALUES (1, 3, 500.00, '2024-01-15'),
       (2, 5, 100.00, '2024-02-20'),
       (3, 10, 1000.00, '2024-03-10');

-- Reset sequences past inserted IDs
SELECT setval('time_deposits_seq', 100);
SELECT setval('withdrawals_seq', 100);
