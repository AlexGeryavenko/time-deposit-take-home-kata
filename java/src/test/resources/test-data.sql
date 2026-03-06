-- Test data: 3 deposits for integration tests
INSERT INTO time_deposits (id, plan_type, days, balance)
VALUES (1, 'basic', 31, 1000.00),
       (2, 'student', 31, 2000.00),
       (3, 'premium', 46, 10000.00);

INSERT INTO withdrawals (id, time_deposit_id, amount, date)
VALUES (1, 1, 100.00, '2024-01-15');

SELECT setval('time_deposits_seq', 100);
SELECT setval('withdrawals_seq', 100);
